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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "nodeDescription")
@XmlType(propOrder = { "id", "type", "name", "artifactClass", "leaf", "nodes" })
public class NodeDescription {

	private String type, name, artifactClass, id;
	private boolean leaf;
	private List<NodeDescription> nodes;

	public NodeDescription() {
		// auto construct
	}
	
	public NodeDescription(String id, String name, String type, String artifactClass, boolean leaf) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.artifactClass = artifactClass;
		this.leaf = leaf;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getArtifactClass() {
		return artifactClass;
	}
	public void setArtifactClass(String artifactClass) {
		this.artifactClass = artifactClass;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public List<NodeDescription> getNodes() {
		return nodes;
	}
	public void setNodes(List<NodeDescription> nodes) {
		this.nodes = nodes;
	}

}
