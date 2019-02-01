/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.events;

import com.github.dozermapper.core.Mapper;

/**
 * Event types which can be triggered
 */
public enum EventTypes {

    /**
     * {@link Mapper} has started processing
     */
    MAPPING_STARTED,

    /**
     * {@link Mapper} is about to write the destination value
     */
    MAPPING_PRE_WRITING_DEST_VALUE,

    /**
     * {@link Mapper} has written the destination value
     */
    MAPPING_POST_WRITING_DEST_VALUE,

    /**
     * {@link Mapper} has finished processing
     */
    MAPPING_FINISHED
}
