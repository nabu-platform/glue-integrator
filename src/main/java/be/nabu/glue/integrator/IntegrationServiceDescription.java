package be.nabu.glue.integrator;

import java.util.ArrayList;
import java.util.List;

import be.nabu.glue.api.ParameterDescription;
import be.nabu.glue.impl.SimpleMethodDescription;

public class IntegrationServiceDescription extends SimpleMethodDescription {

	private ServiceDescription description;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IntegrationServiceDescription(ServiceDescription description) {
		super(null, description.getId(), null, 
				description.getInputs() == null ? new ArrayList<ParameterDescription>() : (List) description.getInputs(), 
				description.getOutputs() == null ? new ArrayList<ParameterDescription>() : (List) description.getOutputs());
		this.description = description;
	}

	public ServiceDescription getServiceDescription() {
		return description;
	}
	
}
