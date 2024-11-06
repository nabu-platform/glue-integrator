/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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
