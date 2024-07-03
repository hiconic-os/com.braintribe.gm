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
package com.braintribe.model.processing.template.evaluation;

import com.braintribe.model.processing.template.preprocessing.TemplatePreprocessing;
import com.braintribe.model.template.Template;

public class TemplateEvaluationContext {
	
	private TemplatePreprocessing templatePreprocessing;
	private TemplateEvaluation templateEvaluation;
	private Template template;
	private boolean useFormular;
	private boolean cloneToPersistenceSession;
	
	public TemplatePreprocessing getTemplatePreprocessing() {
		return templatePreprocessing;
	}
	public void setTemplatePreprocessing(TemplatePreprocessing templatePreprocessing) {
		this.templatePreprocessing = templatePreprocessing;
	}
	public TemplateEvaluation getTemplateEvaluation() {
		return templateEvaluation;
	}
	public void setTemplateEvaluation(TemplateEvaluation templateEvaluation) {
		this.templateEvaluation = templateEvaluation;
	}
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public boolean getUseFormular() {
		return useFormular;
	}
	public void setUseFormular(boolean useFormular) {
		this.useFormular = useFormular;
	}
	public void setCloneToPersistenceSession(boolean cloneToPersistenceSession) {
		this.cloneToPersistenceSession = cloneToPersistenceSession;
	}
	public boolean getCloneToPersistenceSession() {
		return cloneToPersistenceSession;
	}	
}
