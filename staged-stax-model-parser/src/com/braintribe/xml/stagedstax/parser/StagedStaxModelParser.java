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
package com.braintribe.xml.stagedstax.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.braintribe.logging.Logger;

public class StagedStaxModelParser {
	private static Logger log = Logger.getLogger(StagedStaxModelParser.class);
	private static XMLInputFactory inputFactory;

	static {
		inputFactory = XMLInputFactory.newInstance();

		boolean debug = log.isDebugEnabled();
		try {
			inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false); // This disables DTDs entirely for that factory
		} catch(Exception e) {
			if (debug) log.debug("Could not set feature "+XMLInputFactory.SUPPORT_DTD+"=false", e);
		}

		try {
			inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false); // disable external entities
		} catch(Exception e) {
			if (debug) log.debug("Could not set feature javax.xml.stream.isSupportingExternalEntities=false", e);
		}
	}
	
	public <T> T read( File file, ContentHandler<T> handler) throws StagedStaxModelParserException{
		try ( InputStream in = new FileInputStream( file)) {
			XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
			handler.read(streamReader);
			streamReader.close();
			return handler.getResult();
		} catch (FileNotFoundException e) {
			String msg = "cannot find file [" + file.getAbsolutePath() + "]";
			throw new StagedStaxModelParserException(msg, e);
		} catch (IOException e) {
			String msg = "cannot open input stream to [" + file.getAbsolutePath() + "]";
			throw new StagedStaxModelParserException(msg, e);
		} catch (XMLStreamException e) {
			String msg = "cannot read [" + file.getAbsolutePath() + "]";
			throw new StagedStaxModelParserException(msg, e);
		}
		
	}
	
	public <T> T read( InputStream in, ContentHandler<T> handler) throws StagedStaxModelParserException{
		try  {
			XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
			handler.read(streamReader);
			streamReader.close();
			return handler.getResult();
		}  catch (XMLStreamException e) {
			String msg = "cannot read input stream";
			throw new StagedStaxModelParserException(msg, e);
		}
		
	}
	
	public <T> T read( String content, ContentHandler<T> handler) throws StagedStaxModelParserException{
		try  {
			StringReader stringReader = new StringReader( content);
			XMLStreamReader streamReader = inputFactory.createXMLStreamReader( stringReader);
			handler.read(streamReader);
			streamReader.close();
			return handler.getResult();
		}  catch (XMLStreamException e) {
			String msg = "cannot read input stream";
			throw new StagedStaxModelParserException(msg, e);
		}
		
	}
}
