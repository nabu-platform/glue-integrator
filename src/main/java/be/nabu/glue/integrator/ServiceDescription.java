package be.nabu.glue.integrator;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import be.nabu.glue.impl.SimpleParameterDescription;

@XmlRootElement(name = "nodeDescription")
@XmlType(propOrder = { "inputs", "outputs", "inputName", "outputName" })
public class ServiceDescription extends NodeDescription {
	
	private List<SimpleParameterDescription> inputs, outputs;
	private String inputName, outputName;

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
	public String getInputName() {
		return inputName;
	}
	public void setInputName(String inputName) {
		this.inputName = inputName;
	}
	public String getOutputName() {
		return outputName;
	}
	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
}
