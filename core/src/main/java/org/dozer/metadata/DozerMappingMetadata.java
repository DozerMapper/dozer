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

import java.util.ArrayList;
import java.util.List;

import org.dozer.classmap.ClassMap;
import org.dozer.classmap.ClassMappings;

/**
 * <br>
 * @author  florian.kunz
 */
public final class DozerMappingMetadata implements MappingMetadata {
	
	private final ClassMappings classMappings;

	public DozerMappingMetadata(ClassMappings classMappings) {
		this.classMappings = classMappings;
	}

	public List<ClassMappingMetadata> getClassMappings() {
		List<ClassMappingMetadata> classMapCats = new ArrayList<ClassMappingMetadata>();
		
		for(ClassMap classMap : classMappings.getAll().values()) {
			classMapCats.add(new DozerClassMappingMetadata(classMap));
		}
		return classMapCats;
	}

	public List<ClassMappingMetadata> getClassMappingsBySource(String sourceClassName) {
		List<ClassMappingMetadata> classMapCats = new ArrayList<ClassMappingMetadata>();
		if (sourceClassName == null) {
			return classMapCats;
		}
		
		for(ClassMap classMap : classMappings.getAll().values()) {
			if (classMap.getSrcClassName().equals(sourceClassName)) {
				classMapCats.add(new DozerClassMappingMetadata(classMap));
			}
		}
		return classMapCats;
	}

	public List<ClassMappingMetadata> getClassMappingsByDestination(String destinationClassName) {
		List<ClassMappingMetadata> classMapCats = new ArrayList<ClassMappingMetadata>();
		if (destinationClassName == null) {
			return classMapCats;
		}
		
		for(ClassMap classMap : classMappings.getAll().values()) {
			if (classMap.getDestClassName().equals(destinationClassName)) {
				classMapCats.add(new DozerClassMappingMetadata(classMap));
			}
		}
		return classMapCats;
	}

	public ClassMappingMetadata getClassMapping(String sourceClassName, String destinationClassName) {
		if (sourceClassName == null || destinationClassName == null) {
			return null; // TODO Throw exception instead
		}
		
		for(ClassMap classMap : classMappings.getAll().values()) {
			if (classMap.getSrcClassName().equals(sourceClassName)
					|| classMap.getDestClassName().equals(destinationClassName)) {
				return new DozerClassMappingMetadata(classMap);
			}
		}
		return null; // TODO Throw exception instead
	}

}
