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
package com.braintribe.gm.jdbc.impl.column;

import static com.braintribe.utils.lcd.CollectionTools2.index;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import com.braintribe.gm.jdbc.api.GmSelectionContext;
import com.braintribe.gm.jdbc.impl.column.AbstractGmColumn.AbstractDelegatingGmColumn;

/**
 * @author peter.gazdik
 */
public class EnumColumn<E extends Enum<E>> extends AbstractDelegatingGmColumn<E, String> {

	private final Map<String, E> constants;
	private final Class<E> enumClass;

	public EnumColumn(AbstractGmColumn<String> stringColumn, Class<E> enumClass) {
		super(stringColumn);
		this.enumClass = enumClass;

		this.constants = index(enumClass.getEnumConstants()).by(Enum::name).unique();
	}

	@Override
	protected Class<E> type() {
		return enumClass;
	}

	@Override
	protected E tryGetValue(ResultSet rs, GmSelectionContext context) throws Exception {
		String enumName = delegate.tryGetValue(rs, context);
		return constants.get(enumName);
	}

	@Override
	protected void tryBind(PreparedStatement statement, int index, E enumConstant) throws Exception {
		String s = enumConstant == null ? null : enumConstant.name();
		delegate.tryBind(statement, index, s);
	}

}
