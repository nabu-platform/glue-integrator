package be.nabu.glue.integrator;

import java.util.List;

import be.nabu.glue.impl.SimpleMethodDescription;

public class IntegrationServiceDescription extends SimpleMethodDescription {

	private ServiceDescription description;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IntegrationServiceDescription(ServiceDescription description) {
		super(null, description.getId(), null, 
				(List) description.getInputs(), 
				(List) description.getOutputs());
		this.description = description;
	}

	public ServiceDescription getServiceDescription() {
		return description;
	}
	
}
