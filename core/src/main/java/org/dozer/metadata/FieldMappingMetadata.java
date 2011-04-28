/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dozer.metadata;

import org.dozer.classmap.MappingDirection;

/**
 * This interface can be used to obtain the information about the mapping between two fields.
 * @author  florian.kunz
 */
public interface FieldMappingMetadata {

	String getSourceType();
	String getDestinationType();
	
	String getSourceName();
	String getDestinationName();
	
	String getSourceFieldGetMethod();
	String getSourceFieldSetMethod();
	String getDestinationFieldGetMethod();
	String getDestinationFieldSetMethod();
	
	boolean isCopyByReference();
	boolean isSourceFieldAccessible();
	boolean isDestinationFieldAccessible();
	
	MappingDirection getMappingDirection();
	String getDateFormat();
	String getCustomConverter();
	
}
