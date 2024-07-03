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
package com.braintribe.xml.stagedstax.parser;

import java.util.Stack;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.Attributes;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.xml.stagedstax.parser.experts.ContentExpert;
import com.braintribe.xml.stagedstax.parser.experts.SkippingExpert;
import com.braintribe.xml.stagedstax.parser.registry.ContentExpertRegistry;

public class ContentHandler<T>  {
	private static Logger log = Logger.getLogger(ContentHandler.class);
	private ContentExpertRegistry registry;
	private Stack<ContentExpert> stack = new Stack<ContentExpert>();

	private Object result;
	
	
	@Configurable @Required
	public void setRegistry(ContentExpertRegistry registry) {
		this.registry = registry;
	}
	
	
	public void read( XMLStreamReader reader) throws XMLStreamException {
		while (reader.hasNext()) {
			switch (reader.getEventType()) {
				case XMLStreamConstants.START_ELEMENT : {
					startElement( reader.getNamespaceURI(), reader.getLocalName(), reader.getName().getLocalPart(), null);
					break;
				}
				case XMLStreamConstants.END_ELEMENT : {
					endElement( reader.getNamespaceURI(), reader.getLocalName(), reader.getName().toString());
					break;
				}
				
				case XMLStreamConstants.CHARACTERS : {
					characters( reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
					break;
				}
				
				case XMLStreamConstants.PROCESSING_INSTRUCTION: {
					processingInstruction( reader.getPITarget(), reader.getPIData());
					break;
				}
				default: 
					break;
			}
			reader.next();
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) {
	
		// expert 
		ContentExpert parent = null;
		if (!stack.isEmpty() ) {
			parent = stack.peek();
		}		
		
		ContentExpert expert;
		if (parent instanceof SkippingExpert == false) { 
			expert = registry.pushTag( qName);		
			if (expert == null) {
				expert = new SkippingExpert();
				String msg = "no expert found for tag [" + qName + "], skipping";
				if (log.isDebugEnabled()) {
					log.debug( msg);				
				}			
			}
		}
		else {
			// if parent's a skipping expert, the child must be one too
			expert = new SkippingExpert();
		}
		expert.setTag(qName);						
		expert.startElement( parent, uri, localName, qName, atts);
		stack.push(expert);
	}




	
	public void endElement(String uri, String localName, String qName) {
		
		ContentExpert expert = stack.pop();
		if (expert instanceof SkippingExpert == false) { 
			registry.popTag(qName);
		}
		if (stack.isEmpty() == false) {
			expert.endElement(stack.peek(), uri, localName, qName);
		} else {
			expert.endElement(null, uri, localName, qName);
			result = expert.getPayload();
		}						
	}

	public void characters(char[] ch, int start, int length)  {
		stack.peek().characters(ch, start, length);
	}
	
	@SuppressWarnings("unchecked")
	public T getResult() {
		return (T) result;
	}


	public void processingInstruction(String target, String data) {
		// questionable whether it should be that long.. 
		
		// expert 
		ContentExpert parent = null;
		if (!stack.isEmpty() ) {
			parent = stack.peek();
		}		
		
		ContentExpert expert;
		if (parent instanceof SkippingExpert == false) { 
			expert = registry.pushTag( "?" + target);		
			if (expert == null) {
				expert = new SkippingExpert();
				String msg = "no expert found for tag [?" + target + "], skipping";
				if (log.isDebugEnabled()) {
					log.debug( msg);				
				}			
			}
		}
		else {
			// if parent's a skipping expert, the child must be one too
			expert = new SkippingExpert();
		}
		expert.setTag(target);						
		
		expert.startElement( parent, null, target, target, null);
		expert.characters(data.toCharArray(), 0, data.length());
		
		if (stack.isEmpty() == false) {
			expert.endElement(stack.peek(), null, target, target);
		} else {
			expert.endElement(null, null, target, target);
			result = expert.getPayload();
		}			
		if (expert instanceof SkippingExpert == false) { 
			registry.popTag(target);
		}
	}
	
	

}
