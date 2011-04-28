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

import java.util.List;

import org.dozer.classmap.MappingDirection;

/**
 * This interface can be used to obtain information about the mapping definition 
 * between two classes. 
 * @author  florian.kunz
 */
public interface ClassMappingMetadata {
	
	String getSourceClassName();
	String getDestinationClassName();
	Class<?> getSourceClass();
	Class<?> getDestinationClass();
	
	boolean isStopOnErrors();
	boolean isTrimStrings();
	boolean isWildcard();
	boolean isSourceMapNull();
	boolean isDestinationMapNull();
	boolean isSourceMapEmptyString();
	boolean isDestinationMapEmptyString();
	
	String getDateFormat();
	MappingDirection getMappingDirection();
	String getMapId();
	
	List<FieldMappingMetadata> getFieldMappings();
	FieldMappingMetadata getFieldMappingBySource(String sourceFieldName);
	FieldMappingMetadata getFieldMappingByDestination(String destinationFieldName);
}
