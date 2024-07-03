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
package com.braintribe.model.io.metamodel;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.braintribe.codec.dom.genericmodel.GenericModelRootDomCodec;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.meta.GmMetaModel;

/**
 * 
 */
public class ModelSourceWriter {

	private final String filePath;
	private final String outputFolder;
	private final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	private ModelSourceWriter(String[] args) {
		this.filePath = args[0];
		this.outputFolder = args[1];
	}

	private void generate() {
		System.out.println("Loading meta-model from: " + filePath);
		GmMetaModel metaModel = loadModel();
		
		GmSourceWriter writerService = new GmSourceWriter();

		writerService.setOutputDirectory(new File(outputFolder));
		writerService.setGmMetaModel(metaModel);
		writerService.enableWritingSourcesForExistingClasses();

		System.out.println("Rendering model sources into '" + outputFolder + "'");
		writerService.writeMetaModelToDirectory();
		System.out.println("Rendering finished successfully!");
	}

	private GmMetaModel loadModel() {
		GmMetaModel rootObject = null;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(filePath);
			rootObject = getCodec().decode(document);

		} catch (Exception e) {
			throw new RuntimeException("error while loading the model", e);
		}

		return rootObject;
	}

	private GenericModelRootDomCodec<GmMetaModel> getCodec() {
		GenericModelRootDomCodec<GmMetaModel> codec = new GenericModelRootDomCodec<GmMetaModel>();
		codec.setType(getRootType());
		return codec;
	}

	private GenericModelType getRootType() {
		return typeReflection.getType("com.braintribe.model.meta.GmMetaModel");
	}

	/**
	 * As args expects:
	 * args[0] - path to model which should be written ((full) name of metamodel.xml file)
	 * args[1] - output folder (full) path  
	 */
	public static void main(String[] args) {
		new ModelSourceWriter(args).generate();
	}

}
