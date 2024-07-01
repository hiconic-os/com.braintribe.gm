// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.folder;

import java.util.List;
import java.util.Set;

import com.braintribe.model.acl.HasAcl;
import com.braintribe.model.descriptive.HasName;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.resource.Icon;

/**
 * @author gunther.schenk
 */
@SelectiveInformation("${displayName}")
public interface Folder extends HasName, HasAcl {

	EntityType<Folder> T = EntityTypes.T(Folder.class);

	Folder getParent();
	void setParent(Folder parent);

	List<Folder> getSubFolders();
	void setSubFolders(List<Folder> subFolders);

	@Mandatory
	@Override
	String getName();
	@Override
	void setName(String name);

	@Mandatory
	LocalizedString getDisplayName();
	void setDisplayName(LocalizedString displayName);

	Set<String> getTags();
	void setTags(Set<String> tags);

	Icon getIcon();
	void setIcon(Icon icon);

	FolderContent getContent();
	void setContent(FolderContent content);

	default Folder initFolder(String name, String displayName) {
		GmSession session = session();
		if (session != null)
			setDisplayName(session.create(LocalizedString.T).putDefault(displayName));
		else
			setDisplayName(LocalizedString.create(displayName));

		setName(name);
		return this;
	}

	default Folder initFolder(String name, LocalizedString displayName) {
		setDisplayName(displayName);
		setName(name);
		return this;
	}

	static Folder create(String name, String displayName) {
		return T.create().initFolder(name, displayName);
	}

	static Folder create(String name, LocalizedString displayName) {
		return T.create().initFolder(name, displayName);
	}
}
