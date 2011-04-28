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

/*
 * Copyright 2005-2011 the original author or authors.
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

/**
 * Used to query basic mapping information.
 * 
 * @author florian.kunz
 */
public interface MappingMetadata {

  // TODO This is top level API - Document all methods
  // TODO Add alternative Class<?> based methods for lookups

	List<ClassMappingMetadata> getClassMappings();
	List<ClassMappingMetadata> getClassMappingsBySource(String sourceClassName);
	List<ClassMappingMetadata> getClassMappingsByDestination(String destinationClassName);
	ClassMappingMetadata getClassMapping(String sourceClassName, String destinationClassName);

}
