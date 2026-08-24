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
package com.braintribe.gm.jdbc.impl;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static java.util.Collections.emptyList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.braintribe.exception.Exceptions;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmDb;
import com.braintribe.logging.Logger;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.util.jdbc.dialect.DbVariant;

/**
 * @author peter.gazdik
 */
/* package */ class PgBlobCleanupTools {

	private static final Logger log = Logger.getLogger(PgBlobCleanupTools.class);

	private final String tableName;
	private final GmDb db;
	private final Set<GmColumn<?>> columns;

	private final List<String> blobColumnsToCleanUp;

	public PgBlobCleanupTools(GmTableImpl gmTable) {
		this.tableName = gmTable.tableName;
		this.db = gmTable.db;
		this.columns = gmTable.getColumns();
		this.blobColumnsToCleanUp = this.resolveBlobColumns();
	}

	// ####################################################
	// ## . . . . . . . . BLOB cleanup . . . . . . . . . ##
	// ####################################################

	/* Arbitrary constant which merely separates our advisory locks from those taken by anybody else. Spells "GmDb". */
	private static final int ADVISORY_LOCK_NAMESPACE = 0x476D4462;

	/** PostgreSQL truncates identifiers longer than this, which would make two long table names collide. */
	private static final int MAX_IDENTIFIER_LENGTH = 63;

	/** @return the BLOB columns to be cleaned up, or an empty list if there is nothing to do, whatever the reason. */
	public boolean needsBlobCleanup() {
		return !blobColumnsToCleanUp.isEmpty();
	}

	private List<String> resolveBlobColumns() {
		if (!db.blobCleanup)
			return emptyList();

		// On every other supported DB the BLOB is part of the row and is deleted with it, so there is nothing to do.
		if (db.dialect.knownDbVariant() != DbVariant.postgre)
			return emptyList();

		return columns.stream() //
				.map(GmColumn::getBlobSqlColumns) //
				.flatMap(List::stream) //
				.collect(Collectors.toList());
	}

	/**
	 * Ensures a trigger which deletes the Large Objects referenced by this table's {@link GmColumn#getBlobSqlColumns() BLOB columns} when a row is
	 * deleted, or when the reference is replaced by an update.
	 * <p>
	 * This is only relevant for PostgreSQL, where a BLOB column is of type <tt>oid</tt> and thus holds nothing but a reference to a Large Object
	 * stored separately. Deleting the row deletes the reference, not the object, so without this trigger every deleted row leaks a Large Object. On
	 * every other supported DB the BLOB is part of the row and is deleted with it, so nothing is created here.
	 * <p>
	 * We do this in the DB rather than in Java because a delete does not have to go through code which knows about the Large Object - see
	 * {@link GmTableImpl#delete()}, which deletes by condition and never reads the BLOB columns. A row trigger covers every delete, whoever issues
	 * it.
	 * <p>
	 * NOTE that <tt>TRUNCATE</tt> does not fire row triggers, so truncating such a table still leaks every Large Object in it.
	 */
	public void ensureBlobCleanup(Connection c) {
		// gmlo = GM Large Object
		String functionName = blobCleanupName("gmlo_fn");
		String triggerName = blobCleanupName("gmlo_tr");

		ensureBlobCleanupFunction(c, functionName);
		ensureBlobCleanupTrigger(c, triggerName, functionName);
	}

	/**
	 * Serializes the nodes which set up the BLOB cleanup artifacts for this table, so that neither the CREATE OR REPLACE FUNCTION nor the CREATE
	 * TRIGGER can conflict with another node doing the same. Being an xact lock it is released automatically when this transaction ends.
	 */
	public void lockTableAdvisory(Connection c) {
		String sql = "select pg_advisory_xact_lock(" + ADVISORY_LOCK_NAMESPACE + ", " + tableName.hashCode() + ")";

		JdbcTools.withStatement(c, () -> "Locking table for BLOB cleanup setup: " + tableName, s -> {
			s.executeQuery(sql).close();
		});
	}

	/**
	 * The function holds everything that can change, i.e. the list of BLOB columns, while the {@link #ensureBlobCleanupTrigger trigger} merely binds
	 * it to the table. The function can thus be replaced atomically with CREATE OR REPLACE, so adding a BLOB column later never leaves a moment where
	 * the table has no cleanup - as dropping and re-creating anything would.
	 */
	private void ensureBlobCleanupFunction(Connection c, String functionName) {
		String body = blobCleanupFunctionBody();

		// The normal case on every restart: nothing changed, so we do no DDL at all.
		// This also means we never risk the "tuple concurrently updated" error of a concurrent CREATE OR REPLACE.
		if (body.equals(currentFunctionBody(c, functionName))) {
			log.trace(() -> "BLOB cleanup function " + functionName + " is already up to date.");
			return;
		}

		// $GmDb$ rather than $$ so the body may contain dollar quotes of its own
		String sql = "CREATE OR REPLACE FUNCTION " + functionName + "() RETURNS TRIGGER AS $GmDb$" + body + "$GmDb$ LANGUAGE plpgsql";

		JdbcTools.withStatement(c, () -> "Creating BLOB cleanup function: " + functionName, s -> {
			log.debug(() -> "Creating BLOB cleanup function with statement: " + sql);
			execute(s, sql);
		});
	}

	/** @return the body of given function as it is currently stored in the DB, or <tt>null</tt> if there is no such function. */
	private String currentFunctionBody(Connection c, String functionName) {
		String sql = "select prosrc from pg_proc where proname = ? and pg_function_is_visible(oid)";

		String[] result = { null };

		JdbcTools.withPreparedStatement(c, sql, asList(functionName), () -> "Resolving body of function: " + functionName, ps -> {
			ps.setString(1, functionName);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					result[0] = rs.getString(1);
			}
		});

		return result[0];
	}

	/**
	 * We branch on TG_OP - the trigger's operation, i.e. 'DELETE' or 'UPDATE' here - rather than writing a single condition, because NEW is not
	 * assigned for a DELETE, and plpgsql guarantees no short-circuit evaluation which would keep us from touching it.
	 * <p>
	 * For a single BLOB column called "body_blob" the result looks like this:
	 *
	 * <pre>
	 * -- Generated by GmDb. Deletes Large Objects whose referencing row is gone.
	 * BEGIN
	 *     IF TG_OP = 'DELETE' THEN
	 *         IF OLD.body_blob IS NOT NULL THEN
	 *             BEGIN
	 *                 PERFORM lo_unlink(OLD.body_blob);
	 *             EXCEPTION WHEN undefined_object THEN NULL;
	 *             END;
	 *         END IF;
	 *     ELSE
	 *         IF OLD.body_blob IS NOT NULL AND NEW.body_blob IS DISTINCT FROM OLD.body_blob THEN
	 *             BEGIN
	 *                 PERFORM lo_unlink(OLD.body_blob);
	 *             EXCEPTION WHEN undefined_object THEN NULL;
	 *             END;
	 *         END IF;
	 *     END IF;
	 *     RETURN NULL;
	 * END;
	 * </pre>
	 *
	 * With more BLOB columns each branch simply contains one such IF per column.
	 */
	private String blobCleanupFunctionBody() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n-- Generated by GmDb. Deletes Large Objects whose referencing row is gone.\n");
		sb.append("BEGIN\n");
		sb.append("\tIF TG_OP = 'DELETE' THEN\n");
		for (String blobColumn : blobColumnsToCleanUp)
			appendUnlink(sb, blobColumn, false);
		sb.append("\tELSE\n");
		for (String blobColumn : blobColumnsToCleanUp)
			appendUnlink(sb, blobColumn, true);
		sb.append("\tEND IF;\n");
		sb.append("\tRETURN NULL;\n");
		sb.append("END;\n");

		return sb.toString();
	}

	private void appendUnlink(StringBuilder sb, String blobColumn, boolean isUpdate) {
		String old = "OLD." + blobColumn;

		// A row might well store its value outside the BLOB column - see GmDb.resource(String) - and then there is nothing to unlink.
		String condition = old + " IS NOT NULL";
		if (isUpdate)
			condition += " AND NEW." + blobColumn + " IS DISTINCT FROM " + old;

		sb.append("\t\tIF ").append(condition).append(" THEN\n");
		sb.append("\t\t\tBEGIN\n");
		sb.append("\t\t\t\tPERFORM lo_unlink(").append(old).append(");\n");
		// The Large Object can be gone already, e.g. swept by vacuumlo before this trigger existed.
		// Without tolerating that, such a row could never be deleted.
		sb.append("\t\t\tEXCEPTION WHEN undefined_object THEN NULL;\n");
		sb.append("\t\t\tEND;\n");
		sb.append("\t\tEND IF;\n");
	}

	/**
	 * AFTER rather than BEFORE, so we only unlink once the row operation is certain to happen. A BEFORE trigger of somebody else could still cancel
	 * it, and we'd have deleted a Large Object which is still referenced.
	 * <p>
	 * The EXCEPTION block makes this safe even without the advisory lock, as plpgsql implements it with a savepoint, i.e. the failed CREATE does not
	 * abort our transaction.
	 */
	private void ensureBlobCleanupTrigger(Connection c, String triggerName, String functionName) {
		// Not just an optimization: CREATE TRIGGER takes a SHARE ROW EXCLUSIVE lock on the table, which blocks writers.
		// Without this check every node would do that on every single startup, only to find the trigger already there.
		if (blobCleanupTriggerExists(c, triggerName)) {
			log.trace(() -> "BLOB cleanup trigger " + triggerName + " already exists.");
			return;
		}

		String sql = """
				DO $GmDb$
				BEGIN
					CREATE TRIGGER TRIGGER_NAME
						AFTER DELETE OR UPDATE ON TABLE_NAME
						FOR EACH ROW EXECUTE FUNCTION FUNCTION_NAME();
				EXCEPTION WHEN duplicate_object THEN NULL;
				END
				$GmDb$""" //
				.replace("TRIGGER_NAME", triggerName) //
				.replace("TABLE_NAME", tableName) //
				.replace("FUNCTION_NAME", functionName);

		JdbcTools.withStatement(c, () -> "Creating BLOB cleanup trigger: " + triggerName, s -> {
			log.debug(() -> "Ensuring BLOB cleanup trigger with statement: " + sql);
			execute(s, sql);
		});
	}

	private static void execute(Statement s, String sql) throws Exception {
		try {
			s.execute(sql);
		} catch (Exception e) {
			throw Exceptions.contextualize(e, "Executing statement: " + sql);
		}
	}

	private boolean blobCleanupTriggerExists(Connection c, String triggerName) {
		String sql = "select 1 from pg_trigger where tgname = ? and tgrelid = ?::regclass";

		boolean[] result = { false };

		JdbcTools.withPreparedStatement(c, sql, asList(triggerName, tableName), () -> "Checking trigger: " + triggerName, ps -> {
			ps.setString(1, triggerName);
			ps.setString(2, tableName);

			try (ResultSet rs = ps.executeQuery()) {
				result[0] = rs.next();
			}
		});

		return result[0];
	}

	/**
	 * Derives an identifier which fits PostgreSQL's {@value #MAX_IDENTIFIER_LENGTH} character limit. The hash of the full table name is part of it,
	 * so two tables whose names only differ beyond the truncation point still get different identifiers.
	 */
	private String blobCleanupName(String suffix) {
		String suffixPart = "_" + suffix + "_" + String.format("%08x", tableName.hashCode());

		int maxPrefixLength = MAX_IDENTIFIER_LENGTH - suffixPart.length();
		String prefix = tableName.length() <= maxPrefixLength ? tableName : tableName.substring(0, maxPrefixLength);

		return (prefix + suffixPart).toLowerCase();
	}
}
