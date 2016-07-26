package be.nabu.glue.integrator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamSource;

import be.nabu.glue.ScriptRuntime;
import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.MethodDescription;
import be.nabu.glue.api.MethodProvider;
import be.nabu.glue.api.ParameterDescription;
import be.nabu.glue.impl.SimpleExecutionEnvironment;
import be.nabu.glue.xml.XMLMethods;
import be.nabu.libs.evaluator.EvaluationException;
import be.nabu.libs.evaluator.api.Operation;
import be.nabu.libs.evaluator.base.BaseMethodOperation;
import be.nabu.libs.types.map.MapContent;
import be.nabu.utils.io.IOUtils;

public class IntegratorMethodProvider implements MethodProvider {

	private static Map<String, Map<String, MethodDescription>> methods = new HashMap<String, Map<String, MethodDescription>>();
	
	@Override
	public Operation<ExecutionContext> resolve(String name) {
		Map<String, MethodDescription> methods = getMethodMap();
		if (methods.containsKey(name)) {
			MethodDescription methodDescription = methods.get(name);
			return new RemoteIntegratorOperation((IntegrationServiceDescription) methodDescription);
		}
		return null;
	}

	@Override
	public List<MethodDescription> getAvailableMethods() {
		return new ArrayList<MethodDescription>(getMethodMap().values());
	}
	
	public static class RemoteIntegratorOperation extends BaseMethodOperation<ExecutionContext> {
		private IntegrationServiceDescription description;
		private String name;
		public RemoteIntegratorOperation(IntegrationServiceDescription description) {
			this.description = description;
			this.name = description.getServiceDescription().getInputName();
			if (this.name == null) {
				this.name = description.getServiceDescription().getId().replaceAll("^.*\\.", "");
			}
		}
		@Override
		public void finish() throws ParseException {
			// do nothing
		}
		@SuppressWarnings("unchecked")
		@Override
		public Object evaluate(ExecutionContext context) throws EvaluationException {
			Map<String, Object> parameters = new HashMap<String, Object>();
			List<ParameterDescription> inputs = description.getParameters();
			for (int i = 1; i < getParts().size(); i++) {
				Operation<ExecutionContext> argumentOperation = ((Operation<ExecutionContext>) getParts().get(i).getContent());
				parameters.put(inputs.get(i - 1).getName(), argumentOperation.evaluate(context));
			}
			try {
				String stringify = XMLMethods.stringify(parameters);
				int indexOf = stringify.indexOf("<anonymous");
				stringify = "<" + name + stringify.substring(indexOf + "<anonymous".length());
				indexOf = stringify.lastIndexOf("</anonymous>");
				// for empty inputs this is not present
				if (indexOf >= 0) {
					stringify = stringify.substring(0, indexOf) + "</" + name + ">";
				}
//				System.out.println("XML INPUT: " + stringify);
				String response = doHTTP("POST", getEndpoint(getEnvironment()) + "/invoke/" + description.getServiceDescription().getId(), stringify);
				if (response == null || response.trim().isEmpty()) {
					return null;
				}
//				System.out.println("RESPONSE: " + response);
				MapContent object = (MapContent) XMLMethods.objectify(response);
				object.getContent().remove("@xmlns:xsi");
				object.getContent().remove("@xmlns");
				return object;
			} 
			catch (Exception e) {
				throw new EvaluationException(e);
			}
		}
	}

	private static Map<String, MethodDescription> getMethodMap() {
		ExecutionEnvironment environment = getEnvironment();
		if (!methods.containsKey(environment.getName())) {
			synchronized(methods) {
				if (!methods.containsKey(environment.getName())) {
					String endpoint = getEndpoint(environment);
//						DescriptionOutput result = doHTTP("POST", endpoint + "/invoke/nabu.utils.reflection.Node.list", "<list><recursive>true</recursive></list>", DescriptionOutput.class);
					Map<String, MethodDescription> list = new HashMap<String, MethodDescription>();
					try {
						ServiceOutput result = doHTTP("POST", endpoint + "/invoke/nabu.utils.reflection.Node.services", "<services><recursive>true</recursive></services>", ServiceOutput.class);
						for (ServiceDescription description : result.getServices()) {
							list.put(description.getId(), new IntegrationServiceDescription(description));
						}
					}
					catch (Exception e) {
						// ignore
					}
					methods.put(environment.getName(), list);
				}
			}
		}
		return methods.get(environment.getName());
	}

	private static String getEndpoint(ExecutionEnvironment environment) {
		String endpoint = environment.getParameters().get("integrator");
		if (endpoint == null) {
			endpoint = "http://localhost:5555";
		}
		return endpoint;
	}
	
	private static ExecutionEnvironment getEnvironment() {
		try {
			ScriptRuntime runtime = ScriptRuntime.getRuntime();
			return runtime == null ? new SimpleExecutionEnvironment("local") : runtime.getExecutionContext().getExecutionEnvironment();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T doHTTP(String method, String endpoint, String content, Class<T> clazz) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			String response = doHTTP(method, endpoint, content);
			return (T) context.createUnmarshaller().unmarshal(new StreamSource(new StringReader(response)));
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String doHTTP(String method, String endpoint, String content) throws IOException {
		URL url = new URL(endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(method);
		if (content != null) {
			byte[] bytes = content.getBytes("UTF-8");
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestProperty("Content-Length", "" + bytes.length);
			connection.setDoOutput(true);
			connection.getOutputStream().write(bytes);
		}
		InputStream stream = connection.getInputStream();
		try {
			String result = new String(IOUtils.toBytes(IOUtils.wrap(stream)), "UTF-8");
			if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
				// a remote stacktrace
				if ("text/plain".equals(connection.getContentType())) {
					throw new RuntimeException("Remote exception: " + result);
				}
				return result;
			}
			else {
				throw new RuntimeException("Remote exception " + connection.getResponseMessage() + ": " + result);
			}
		}
		finally {
			stream.close();
		}
	}
	@XmlRootElement(name = "servicesResponse")
	public static class ServiceOutput {
		private List<ServiceDescription> services;

		public List<ServiceDescription> getServices() {
			return services;
		}

		public void setServices(List<ServiceDescription> services) {
			this.services = services;
		}
	}
	
	@XmlRootElement(name = "listResponse")
	public static class DescriptionOutput {
		private List<NodeDescription> nodes;

		public List<NodeDescription> getNodes() {
			return nodes;
		}
		public void setNodes(List<NodeDescription> nodes) {
			this.nodes = nodes;
		}
	}
}
