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
package com.braintribe.model.processing.notification.api.builder.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;
import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

public abstract class BuilderTools {

	public static String createDetailedMessage(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw) {
			@Override
			public void println() {
				write('\n');
			}
		};
		e.printStackTrace(pw);
		String detailedMsg = sw.toString();
		detailedMsg = (detailedMsg == null || detailedMsg.trim().length() == 0) ? null : detailedMsg;
		return detailedMsg;
	}

	
	public static <T> void receive(T entity, Consumer<T> receiver) {
		try {
			receiver.accept(entity);
		} catch (Exception e) {
			throw new RuntimeException("Error building Notification.", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends GenericEntity> T createEntity (EntityType<? extends GenericEntity> entityType, Function<EntityType<? extends GenericEntity>, GenericEntity> entityFactory) { 
		try {
			return (T) entityFactory.apply(entityType);	
		} catch (Exception e) {
			throw new RuntimeException("Could not create entity for class: "+entityType,e);
		}
	}
	
}
