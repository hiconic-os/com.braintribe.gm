package com.braintribe.messaging.jdbc.demo;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmDb;
import com.braintribe.gm.jdbc.api.GmTable;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.utils.stream.api.StreamPipes;

public class PostgresListenerDemo_Main {

	public static void main(String[] args) throws Exception {
		new PostgresListenerDemo_Main().run();
	}

	private final String tableName = "test_table";

	private final DataSource dataSource = dataSource();
	private final GmDb gmDb = GmDb.newDb(dataSource) //
			.withStreamPipeFactory(StreamPipes.simpleFactory()) //
			.done();

	private final GmColumn<String> colIdStr = gmDb.shortString255("id").primaryKey().notNull().done();
	private final GmColumn<String> colName = gmDb.shortString255("name").done();
	private final GmColumn<Integer> colNumber = gmDb.intCol("number").done();

	private volatile boolean running = true;

	private final GmTable table = gmDb.newTable(tableName) //
			.withColumns( //
					colIdStr, //
					colName, //
					colNumber //
			).done();

	private void run() throws Exception {
		printMain("Ensuring table");
		table.ensure();

		printMain("Ensuring function notify_insert");
		ensureTriggerFunction();

		printMain("Ensuring trigger on ");
		ensureTrigger();

		printMain("Starting listener");
		Thread listener = Thread.ofVirtual().start(this::listen);

		printMain("Sleeping to give listener time to start listening");
		Thread.sleep(100);

		for (int i = 1; i <= 5; i++) {
			printMain("Inserting: " + i);
			table.insert(colIdStr, UUID.randomUUID().toString(), colName, "name" + i + "" + i, colNumber, i);
			printMain("Sleeping: " + i);
			Thread.sleep(1000);
		}

		printMain("Interrupting listener thread");
		running = false;
		listener.interrupt();

		printMain("Waiting for listener thread");
		listener.join();

		printMain("Listener thread terminated");
	}

	private void ensureTriggerFunction() {
		String createFunctionSQL = """
				CREATE OR REPLACE FUNCTION notify_insert() RETURNS TRIGGER AS $$
				BEGIN
				    PERFORM pg_notify('row_inserted', NEW.name || ':' || NEW.number);
				    RETURN NEW;
				END;
				$$ LANGUAGE plpgsql;
				     """;

		JdbcTools.withStatement(dataSource, () -> "Creating FUNCTION notify_insert()", stmt -> {
			stmt.execute(createFunctionSQL);
		});
	}

	private void ensureTrigger() {
		String createTriggerSQL = """
				       DO $$
				       BEGIN
				           IF NOT EXISTS (
				               SELECT 1
				               FROM pg_trigger
				               WHERE tgname = 'after_insert'
				               AND tgrelid = 'TABLE_NAME'::regclass
				           ) THEN
				CREATE TRIGGER after_insert
				AFTER INSERT ON TABLE_NAME
				FOR EACH ROW EXECUTE FUNCTION notify_insert();
				           END IF;
				       END;
				       $$
				   """.replaceAll("TABLE_NAME", tableName);

		JdbcTools.withStatement(dataSource, () -> "Creating TRIGGER after_insert", stmt -> {
			stmt.execute(createTriggerSQL);
		});
	}

	private void listen() {
		try (Connection conn = dataSource.getConnection()) {
			Statement stmt = conn.createStatement();
			stmt.execute("LISTEN row_inserted"); // Subscribe to the channel
			stmt.close();

			while (running) {
				// Check for notifications
				org.postgresql.PGConnection pgConn = conn.unwrap(org.postgresql.PGConnection.class);
				org.postgresql.PGNotification[] notifications = pgConn.getNotifications(0);

				if (notifications != null) 
					for (org.postgresql.PGNotification notification : notifications) 
						printListener("Notified: " + notification.getParameter());
			}

		} catch (Exception e) {
			 if (!running || Thread.currentThread().isInterrupted()) {
				 printListener("Interrupted");
				 return;
			 }
			e.printStackTrace();
		}
	}

	private void printMain(String s) {
		log(s, " [Main] ");
	}

	private void printListener(String s) {
		log(s, " [Listener] ");
	}
	
	private void log(String s, String threadName) {
		System.out.println(currentHourMinuteSecondMs() + threadName + s);
	}

	private String currentHourMinuteSecondMs() {
		return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + "]";
	}

	private static DataSource dataSource() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setServerNames(new String[] { "localhost" });
		dataSource.setPortNumbers(new int[] { 5432 });
		dataSource.setDatabaseName("notify-demo");
		dataSource.setUser("postgres");
		dataSource.setPassword("postgres");
		return dataSource;
	}

}