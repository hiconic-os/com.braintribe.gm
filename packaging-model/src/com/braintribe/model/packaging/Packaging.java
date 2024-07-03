// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.packaging;

import java.util.Date;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * a packaging reflects what is packed in a build process
 * it has a list of dependencies (in terms of the ArtifactModel: solutions and not dependencies)
 * and
 * it has a terminal artifact (the reason why the packaging exists at all)
 * 
 * @author Pit
 *
 */

@SelectiveInformation("${terminalArtifact.artifactId}")
public interface Packaging extends GenericEntity{

	EntityType<Packaging> T = EntityTypes.T(Packaging.class);

	public Date getTimestamp();
	public void setTimestamp( Date date);
	
	public String getRevision();
	public void setRevision(String revision);
	
	public Artifact getTerminalArtifact();
	public void setTerminalArtifact( Artifact artifact);
	
	public List<Dependency> getDependencies();
	public void setDependencies( List<Dependency> artifacts);
	
	public String getMD5();
	public void setMD5( String md5);
	
	/**
	 * The version of the release the terminal belongs to. Examples: <code>1.1, 1.1.6, 2.0</code><br>
	 * Note that this is just a version number (or name). It usually does not match the version of the
	 * {@link #getTerminalArtifact() terminal artifact}. Actually it has nothing to do with artifacts at all. The main
	 * purpose of the version is that it's used in tickets. For example, a customer may report a bug in tribefire
	 * Control Center and include version <code>1.1.5</code> in the ticket description. This is enough information for
	 * Braintribe QA and developers to retrieve all other required data (e.g. dependencies, artifact versions, source
	 * code revisions).
	 */
	public String getVersion();
	public void setVersion(String version);
	
}
