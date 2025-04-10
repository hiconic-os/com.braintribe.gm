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
package com.braintribe.gm.jdbc.impl;

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static com.braintribe.utils.lcd.CollectionTools2.newLinkedSet;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static java.util.Collections.unmodifiableSet;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.braintribe.exception.Exceptions;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmDb;
import com.braintribe.gm.jdbc.api.GmIndex;
import com.braintribe.gm.jdbc.api.GmSelectBuilder;
import com.braintribe.gm.jdbc.api.GmTable;
import com.braintribe.gm.jdbc.api.GmTableBuilder;
import com.braintribe.gm.jdbc.api.GmUpdateBuilder;
import com.braintribe.gm.jdbc.impl.column.AbstractGmColumn;
import com.braintribe.logging.Logger;
import com.braintribe.util.jdbc.JdbcTools;

/**
 * @author peter.gazdik
 */
public class GmTableImpl implements GmTable, GmTableBuilder {

	private static final Logger log = Logger.getLogger(GmTableImpl.class);

	protected final String tableName;
	protected final GmDb db;
	protected GmColumn<?> primaryKeyColumn;

	private final List<GmColumn<?>> columns = newList();
	private final List<GmColumn<?>> mandatoryColumns = newList();
	private final List<GmIndex> indices = newList();

	private Set<GmColumn<?>> readOnlyColumns;
	private Set<GmIndex> readOnlyIndices;

	private final Map<String, GmColumn<?>> columnsByGmName = newMap();
	private final Map<String, GmIndex> indicesByName = newMap();

	public GmTableImpl(String tableName, GmDb db) {
		this.tableName = tableName;
		this.db = db;
	}

	// ####################################################
	// ## . . . . . . . . GmTableBuilder . . . . . . . . ##
	// ####################################################

	@Override
	public GmTableBuilder withColumns(GmColumn<?>... columns) {
		return withColumns(Arrays.asList(columns));
	}

	@Override
	public GmTableBuilder withColumns(Collection<GmColumn<?>> newColumns) {
		adopt(newColumns);

		columns.addAll(newColumns);
		mandatoryColumns.addAll(findMandatoryColumns(newColumns));

		return this;
	}

	private void adopt(Collection<GmColumn<?>> newColumns) {
		for (GmColumn<?> gmColumn : newColumns)
			((AbstractGmColumn<?>) gmColumn).setTable(this);
	}

	private List<GmColumn<?>> findMandatoryColumns(Collection<GmColumn<?>> newColumns) {
		return newColumns.stream() //
				.filter(GmColumn::isNotNull) //
				.filter(c -> !c.isAutoIncrement()) //
				.collect(Collectors.toList());
	}

	@Override
	public GmTableBuilder withIndices(GmIndex... indices) {
		return withIndices(Arrays.asList(indices));
	}

	@Override
	public GmTableBuilder withIndices(Collection<GmIndex> indices) {
		this.indices.addAll(indices);
		return this;
	}

	@Override
	public GmTable done() {
		this.readOnlyColumns = unmodifiableSet(newLinkedSet(columns));
		this.readOnlyIndices = unmodifiableSet(newLinkedSet(indices));

		index();
		return this;
	}

	private void index() {
		for (GmColumn<?> column : columns) {
			GmColumn<?> otherColumn = columnsByGmName.put(column.getGmName(), column);
			if (otherColumn != null)
				throw new IllegalStateException("Multiple columns registered with the same gm name: " + column.getGmName() + ". FIRST: " + column
						+ ", SECOND: " + otherColumn);

			if (column.isPrimaryKey())
				primaryKeyColumn = column;
		}

		for (GmIndex index : indices) {
			GmIndex otherIndex = indicesByName.put(index.getName(), index);
			if (otherIndex != null)
				throw new IllegalStateException(
						"Multiple indices registered with the same gm name: " + index.getName() + ". FIRST: " + index + ", SECOND: " + otherIndex);
		}
	}

	// ####################################################
	// ## . . . . . . . . . . GmTable . . . . . . . . . .##
	// ####################################################

	// @formatter:off
	@Override public String getName() { return tableName; }
	@Override public Set<GmColumn<?>> getColumns() { return readOnlyColumns; }
	@Override public Set<GmIndex> getIndices() { return readOnlyIndices; }
	// @formatter:on

	@Override
	public void ensure() {
		JdbcTools.withManualCommitConnection(db.dataSource, () -> "Ensuring table: " + getName(), this::ensureExists);
	}

	private void ensureExists(Connection c) {
		String table = JdbcTools.tableExists(c, tableName);
		if (table == null) {
			createTableWithAllColumns(c);
			addMissingIndices(c, tableName);
		} else {
			addMissingColumns(c, table);
			addMissingIndices(c, table);
		}
	}

	private void createTableWithAllColumns(Connection c) {
		String createStatement = createTableStatement();
		JdbcTools.withStatement(c, () -> "Creating table: " + tableName + " with statement: " + createStatement, s -> {
			try {
				log.debug(() -> "Creating table with statement: " + createStatement);
				s.executeUpdate(createStatement);
				log.debug(() -> "Successfully created table: " + tableName);

			} catch (Exception e) {
				try {
					if (JdbcTools.tableExists(c, tableName) != null)
						return;
				} catch (Exception e2) {
					e.addSuppressed(e2);
				}
				throw e;
			}
		});
	}

	private String createTableStatement() {
		StringJoiner sj = new StringJoiner(", ", "create table " + tableName + " (", ")");
		for (GmColumn<?> column : columns)
			column.streamSqlColumnDeclarations().forEach(sj::add);

		return sj.toString();
	}

	private void addMissingColumns(Connection c, String sqlTableName) {
		Set<String> existingColumns = JdbcTools.columnsExist(c, sqlTableName, columnNames());

		List<GmColumn<?>> missingColumns = columnNames().stream() //
				.filter(column -> !existingColumns.contains(column)) //
				.map(this::getColumn) //
				.collect(Collectors.toList());

		addColumns(c, sqlTableName, missingColumns);
	}

	/**
	 * @return set of added columns
	 */
	/* package */ Set<GmColumn<?>> addColumns(Connection c, String sqlTableName, List<GmColumn<?>> columns) {
		Set<GmColumn<?>> result = newLinkedSet();
		Map<String, Exception> columnNameToException = newMap();

		JdbcTools.withStatement(c, () -> "Adding missing columns for table: " + sqlTableName, s -> {
			for (GmColumn<?> column : columns) {
				List<String> columnDeclarations = column.streamSqlColumnDeclarations().collect(Collectors.toList());

				for (String declaration : columnDeclarations) {
					String st = "alter table " + sqlTableName + " add " + declaration;

					try {
						log.debug("Executing update statement: " + st);
						executeUpdate(s, st);
						log.debug("Successfully updated " + sqlTableName + ".");

						result.add(column);

					} catch (Exception e) {
						columnNameToException.put(column.getGmName(), e);
					}
				}
			}
		});

		if (columnNameToException.isEmpty())
			return result;

		// Here we check if columns already exist.
		// If they were created by another process,e.g. a different node in the cluster, we'd get an exception but there is no issue

		Set<String> existingColumns = JdbcTools.columnsExist(c, sqlTableName, columnNames());
		columnNameToException.keySet().removeAll(existingColumns);

		if (!columnNameToException.isEmpty())
			throw Exceptions.unchecked(first(columnNameToException.values()), "Error while adding columns to table: " + sqlTableName);

		return result;
	}

	private void addMissingIndices(Connection c, String sqlTableName) {
		Set<String> existingIndices = JdbcTools.indicesExist(c, sqlTableName, indexNames());

		List<GmIndex> missingIndices = indexNames().stream() //
				.filter(index -> !existingIndices.contains(index)) //
				.map(this::getIndex) //
				.collect(Collectors.toList());

		addIndices(c, sqlTableName, missingIndices);
	}

	/**
	 * @return set of added indices
	 */
	/* package */ Set<GmIndex> addIndices(Connection c, String sqlTableName, List<GmIndex> indices) {
		Set<GmIndex> result = newLinkedSet();
		Map<String, Exception> indexNameToException = newMap();

		JdbcTools.withStatement(c, () -> "Adding missing indices to table: " + tableName, s -> {
			for (GmIndex index : indices) {
				String indexStatement = createIndexStatement(index);

				try {
					log.debug("Creating index statement: " + indexStatement);
					executeUpdate(s, indexStatement);
					log.debug("Successfully added index:" + index.getName());

					result.add(index);

				} catch (Exception e) {
					indexNameToException.put(index.getName(), e);
				}
			}
		});

		if (indexNameToException.isEmpty())
			return result;

		// Here we check if indices already exist.
		// If they were created by another process,e.g. a different node in the cluster, we'd get an exception but there is no issue

		Set<String> existingIndices = JdbcTools.indicesExist(c, sqlTableName, indexNames());
		indexNameToException.keySet().removeAll(existingIndices);

		if (!indexNameToException.isEmpty())
			throw Exceptions.unchecked(first(indexNameToException.values()), "Error while adding indices to table: " + sqlTableName);

		return result;
	}

	private String createIndexStatement(GmIndex index) {
		StringJoiner sj = new StringJoiner(", ", "create index " + index.getName() + " on " + tableName + " (", ")");

		index.getColumns().stream() //
				.map(GmColumn::getSingleSqlColumn) //
				.forEach(sj::add);

		return sj.toString();
	}

	/* Some RDBSes like Oracle don't even tell you which column/index was the problem. */
	private static void executeUpdate(Statement s, String sql) throws Exception {
		try {
			s.executeUpdate(sql);
		} catch (Exception e) {
			throw Exceptions.contextualize(e, "Executing statement: " + sql);
		}
	}

	private Set<String> columnNames() {
		return columnsByGmName.keySet();
	}

	private GmColumn<?> getColumn(String gmName) {
		return columnsByGmName.computeIfAbsent(gmName, n -> {
			throw new NoSuchElementException("No column found for name: " + gmName);
		});
	}

	private Set<String> indexNames() {
		return indicesByName.keySet();
	}

	private GmIndex getIndex(String name) {
		return indicesByName.computeIfAbsent(name, n -> {
			throw new NoSuchElementException("No index found for name: " + name);
		});
	}

	@Override
	public void insert(Connection connection, Map<GmColumn<?>, Object> values) {
		GmInsertImpl.insert(this, values, connection);
	}

	@Override
	public GmSelectBuilder select(Set<GmColumn<?>> columns) {
		return new GmSelectBuilderImpl(this, columns);
	}

	@Override
	public GmUpdateBuilder update(Map<GmColumn<?>, Object> columnsToValues) {
		return new GmUpdateBuilderImpl(this, columnsToValues);
	}

	@Override
	public GmUpdateBuilder delete() {
		return new GmDeleteBuilderImpl(this);
	}

	public List<GmColumn<?>> mandatoryColumns() {
		return mandatoryColumns;
	}

}
