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
package com.braintribe.model.processing.aop.impl.aspect;

import com.braintribe.model.extensiondeployment.meta.AccessJoinPoint;
import com.braintribe.model.extensiondeployment.meta.Advice;

public class EnumConversion {
	/**
	 * helper function to convert modeled join point enum values to api one 
	 * @param joinPoint - the {@link AccessJoinPoint} as modeled
	 * @return - the corresponding {@link com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint} 
	 */
	public static AccessJoinPoint convert( com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint joinPoint) {
		if (joinPoint == null)
			return null;
		
		switch ( joinPoint) {
		case applyManipulation:
			return AccessJoinPoint.applyManipulation;
		case getMetaModel:
			return AccessJoinPoint.getMetaModel;
		case getReferences:
			return AccessJoinPoint.getReferences;
		case query:
			return AccessJoinPoint.query;			
		case queryEntities:
			return AccessJoinPoint.queryEntities;
		case queryProperties:
			return AccessJoinPoint.queryProperties;
		default:
			return null;			
		}
	}

	/**
	 * helper function to convert modeled advice enum values to api enum
	 * @param advice - the {@link Advice} as modeled 
	 * @return - the corresponding {@link com.braintribe.model.processing.aop.api.aspect.Advice}
	 */
	public static Advice convert( com.braintribe.model.processing.aop.api.aspect.Advice advice) {
		switch( advice) {
		case after:
			return Advice.after;
		case around:			
			return Advice.around;
		case before:
			return Advice.before;
		default:
			return null;
			
		}
	}
	
	/**
	 * helper function to convert modeled join point enum values to api one 
	 * @param joinPoint - the {@link AccessJoinPoint} as modeled
	 * @return - the corresponding {@link com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint} 
	 */
	public static com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint convert( AccessJoinPoint joinPoint) {
		switch ( joinPoint) {
		case applyManipulation:
				return com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint.applyManipulation;
		case getMetaModel:
			return com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint.getMetaModel;
		case getReferences:
			return com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint.getReferences;
		case query:
			return com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint.query;			
		case queryEntities:
			return com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint.queryEntities;
		case queryProperties:
			return com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint.queryProperties;
		default:
			return null;			
		}
	}

	/**
	 * helper function to convert modeled advice enum values to api enum
	 * @param advice - the {@link Advice} as modeled 
	 * @return - the corresponding {@link com.braintribe.model.processing.aop.api.aspect.Advice}
	 */
	public static com.braintribe.model.processing.aop.api.aspect.Advice convert( Advice advice) {
		switch( advice) {
		case after:
			return com.braintribe.model.processing.aop.api.aspect.Advice.after;
		case around:			
			return com.braintribe.model.processing.aop.api.aspect.Advice.around;
		case before:
			return com.braintribe.model.processing.aop.api.aspect.Advice.before;
		default:
			return null;
			
		}
	}
}
