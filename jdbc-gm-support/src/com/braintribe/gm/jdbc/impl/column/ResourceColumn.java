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

import static com.braintribe.gm.jdbc.api.GmLobLoadingMode.NO_LOB;
import static com.braintribe.gm.jdbc.api.GmLobLoadingMode.ONLY_LOB;
import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import com.braintribe.gm.jdbc.api.GmLobLoadingMode;
import com.braintribe.gm.jdbc.api.GmSelectionContext;
import com.braintribe.gm.jdbc.impl.column.AbstractGmColumn.MultiGmColumn;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.resource.Resource;
import com.braintribe.util.jdbc.dialect.JdbcDialect;
import com.braintribe.utils.IOTools;
import com.braintribe.utils.stream.ReaderInputStream;
import com.braintribe.utils.stream.api.StreamPipe;
import com.braintribe.utils.stream.api.StreamPipeFactory;

/**
 * @author peter.gazdik
 */
public class ResourceColumn extends MultiGmColumn<Resource> {

	public static final String TEXT_PLAIN_MIME_TYPE = "text/plain";

	private static final Logger log = Logger.getLogger(ResourceColumn.class);

	private final JdbcDialect dialect;
	private final int maxChars;
	private final StreamPipeFactory streamPipeFactory;

	private final Map<PreparedStatement, InputStream> openStreams = new WeakHashMap<>();

	public ResourceColumn(String name, JdbcDialect dialect, int maxChars, StreamPipeFactory streamPipeFactory) {
		super(name);
		this.dialect = dialect;
		this.maxChars = maxChars;
		this.streamPipeFactory = streamPipeFactory;
	}

	@Override
	public Stream<String> streamSqlColumnDeclarations() {
		return Stream.of( //
				strColumnName() + " " + dialect.nvarchar(maxChars), //
				blobColumnName() + " " + dialect.blobType());
	}

	@Override
	public List<String> getSqlColumns() {
		return asList( //
				strColumnName(), //
				blobColumnName());
	}

	private String strColumnName() {
		return name;
	}

	private String blobColumnName() {
		return name + "_blob";
	}

	@Override
	protected Class<Resource> type() {
		return Resource.class;
	}

	@Override
	protected boolean tryIsStoredAsLob(ResultSet rs) throws SQLException {
		return rs.getBlob(blobColumnName()) != null;
	}

	@Override
	protected Resource tryGetValue(ResultSet rs, GmSelectionContext context) throws Exception {
		// TODO enrich with size/mimeType...
		return getResourceInstance(rs, context);
	}

	private Resource getResourceInstance(ResultSet rs, GmSelectionContext context) throws Exception {
		GmLobLoadingMode lobLoadingMode = context.lobLoadingMode(this);

		String text = rs.getString(name);
		if (text != null)
			return lobLoadingMode == ONLY_LOB ? null : Resource.createTransient(() -> new ReaderInputStream(new StringReader(text)));

		Blob blob = rs.getBlob(blobColumnName());
		if (blob == null || lobLoadingMode == NO_LOB)
			return null;

		StreamPipe pipe = streamPipeFactory.newPipe("Value of column: " + name);

		try (InputStream is = blob.getBinaryStream(); //
				OutputStream os = pipe.openOutputStream()) {
			IOTools.pump(is, os);
		}

		// NOTICE:
		// We use a real class rather than lambda to make possible troubleshooting simpler.
		return Resource.createTransient(new ResourceColumnInputStreamProvider(pipe));
	}

	private static class ResourceColumnInputStreamProvider implements InputStreamProvider {
		private final StreamPipe pipe;

		public ResourceColumnInputStreamProvider(StreamPipe pipe) {
			this.pipe = pipe;
		}

		@Override
		public InputStream openInputStream() throws IOException {
			return pipe.openInputStream();
		}
	}

	@Override
	protected void tryBind(PreparedStatement ps, int index, Resource value) throws Exception {
		if (value == null) {
			ps.setString(index, null);
			ps.setBlob(index + 1, null, 0);
			return;
		}

		String text = toShortText(value);

		if (text != null) {
			ps.setString(index, text);
			ps.setBlob(index + 1, null, 0);
		} else {
			ps.setString(index, null);
			ps.setBlob(index + 1, inputStream(ps, value));
		}
	}

	private String toShortText(Resource resource) throws Exception {
		String mimeType = resource.getMimeType();
		if (!TEXT_PLAIN_MIME_TYPE.equals(mimeType))
			return null;

		Long size = resource.getFileSize();
		// If size is unknown, we do not try to load the resource.
		// If size is at most three times the maxChars, there is a theoretical chance the string has few enough chars to fit within maxChars
		if (size == null || size > 3 * maxChars)
			return null;

		String text = toString(resource);
		if (text.length() > maxChars)
			return null;

		return text;
	}

	private String toString(Resource resource) throws Exception {
		return NvarcharBlobColumn.inputStreamToString(resource::openStream);
	}

	private InputStream inputStream(PreparedStatement ps, Resource resource) {
		InputStream result = resource.openStream();

		openStreams.put(ps, result);

		return result;
	}

	@Override
	public void afterStatementExecuted(PreparedStatement ps) {
		InputStream is = openStreams.get(ps);
		if (is != null)
			try {
				is.close();
			} catch (Exception e) {
				log.warn("Error while closing inputStream for Resource column '" + name + "'. Statement: " + ps.toString(), e);
			}
	}

}
