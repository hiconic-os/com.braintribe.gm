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
package com.braintribe.model.resource.specification;

import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@SelectiveInformation("PDF Specification")
public interface PdfSpecification extends PhysicalDimensionSpecification, PageCountSpecification {

	final EntityType<PdfSpecification> T = EntityTypes.T(PdfSpecification.class);

	String imageAlignment = "imageAlignment";
	String detectPageRotation = "detectPageRotation";
	String scaleTiffByDpi = "scaleTiffByDpi";
	String ignoreInvalidImages = "ignoreInvalidImages";

	@Name("Image Alignment")
	@Description("The alignment of the image.")
	int getImageAlignment();
	void setImageAlignment(int imageAlignment);

	@Name("Page Rotation")
	@Description("The rotation of the image.")
	boolean getDetectPageRotation();
	void setDetectPageRotation(boolean detectPageRotation);

	@Name("Scale TIFF by DPI")
	@Description("Scale TIFF by DPI.")
	boolean getScaleTiffByDpi();
	void setScaleTiffByDpi(boolean scaleTiffByDpi);

	@Name("Ignore Invalid Images")
	@Description("Ignore Invalid Images.")
	boolean getIgnoreInvalidImages();
	void setIgnoreInvalidImages(boolean ignoreInvalidImages);

}
