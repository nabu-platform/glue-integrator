package be.nabu.glue.integrator;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import be.nabu.glue.impl.SimpleParameterDescription;

@XmlRootElement(name = "nodeDescription")
@XmlType(propOrder = { "inputs", "outputs" })
public class ServiceDescription extends NodeDescription {
	
	private List<SimpleParameterDescription> inputs, outputs;

	public List<SimpleParameterDescription> getInputs() {
		return inputs;
	}
	public void setInputs(List<SimpleParameterDescription> inputs) {
		this.inputs = inputs;
	}

	public List<SimpleParameterDescription> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<SimpleParameterDescription> outputs) {
		this.outputs = outputs;
	}
}
