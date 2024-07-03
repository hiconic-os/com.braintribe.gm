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
package com.braintribe.model.processing.meta.cmd.empty;

import static java.util.Collections.emptyList;

import java.util.List;

import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.CmdRuntimeException;
import com.braintribe.model.processing.meta.cmd.extended.ConstantMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EntityMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EnumMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.MdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.ModelMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.PropertyMdDescriptor;
import com.braintribe.model.processing.meta.cmd.result.ConstantMdResult;
import com.braintribe.model.processing.meta.cmd.result.EntityMdResult;
import com.braintribe.model.processing.meta.cmd.result.EnumMdResult;
import com.braintribe.model.processing.meta.cmd.result.MdResult;
import com.braintribe.model.processing.meta.cmd.result.ModelMdResult;
import com.braintribe.model.processing.meta.cmd.result.PropertyMdResult;

/**
 * @author peter.gazdik
 */
public abstract class EmptyMdResult<M extends MetaData, D extends MdDescriptor> implements MdResult<M, D> {

	@Override
	public M exclusive() throws CmdRuntimeException {
		return null;
	}

	@Override
	public D exclusiveExtended() throws CmdRuntimeException {
		return null;
	}

	@Override
	public List<M> list() throws CmdRuntimeException {
		return emptyList();
	}

	@Override
	public List<D> listExtended() throws CmdRuntimeException {
		return emptyList();
	}

	// Model

	public static class EmptyModelMdResult<M extends MetaData> extends EmptyMdResult<M, ModelMdDescriptor> implements ModelMdResult<M> {
		private static final EmptyModelMdResult<?> instance = new EmptyModelMdResult<MetaData>();

		public static <M extends MetaData> ModelMdResult<M> singleton() {
			return (ModelMdResult<M>) instance;
		}
	}

	// Entity

	public static class EmptyEntityMdResult<M extends MetaData> extends EmptyMdResult<M, EntityMdDescriptor> implements EntityMdResult<M> {
		private static final EmptyEntityMdResult<?> instance = new EmptyEntityMdResult<MetaData>();

		public static <M extends MetaData> EntityMdResult<M> singleton() {
			return (EntityMdResult<M>) instance;
		}
	}

	// Property

	public static class EmptyPropertyMdResult<M extends MetaData> extends EmptyMdResult<M, PropertyMdDescriptor> implements PropertyMdResult<M> {
		private static final EmptyPropertyMdResult<?> instance = new EmptyPropertyMdResult<MetaData>();

		public static <M extends MetaData> PropertyMdResult<M> singleton() {
			return (PropertyMdResult<M>) instance;
		}
	}

	// Enum

	public static class EmptyEnumMdResult<M extends MetaData> extends EmptyMdResult<M, EnumMdDescriptor> implements EnumMdResult<M> {
		private static final EmptyEnumMdResult<?> instance = new EmptyEnumMdResult<MetaData>();

		public static <M extends MetaData> EnumMdResult<M> singleton() {
			return (EnumMdResult<M>) instance;
		}
	}

	// Constant

	public static class EmptyConstantMdResult<M extends MetaData> extends EmptyMdResult<M, ConstantMdDescriptor> implements ConstantMdResult<M> {
		private static final EmptyConstantMdResult<?> instance = new EmptyConstantMdResult<MetaData>();

		public static <M extends MetaData> ConstantMdResult<M> singleton() {
			return (ConstantMdResult<M>) instance;
		}
	}

}
