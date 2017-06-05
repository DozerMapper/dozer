/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer.functional_tests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.vo.TestObject;
import org.dozer.vo.map.MapToMap;
import org.dozer.vo.map.MapToMapPrime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmitry Buzdin
 */
public class MapWithCustomGetAndPutMethodTest extends AbstractFunctionalTest {

	/**
	 * <mapping>
		<class-a is-accessible="true">tsb.portal.controller.base.PortalResponse</class-a>
		<class-b map-set-method="putUnknownType" map-get-method="get">tsb.grandcentral.collection.ObjectMap</class-b>
		<!-- <field> -->
		<!-- <a is-accessible="true">error</a> -->
		<!-- <b key="error">this</b> -->
		<!-- </field> -->
		<field>
			<a>errorCode</a>
			<b key="errorCode">this</b>
		</field>
		<field>
			<a>errorMessage</a>
			<b key="errorMessage">this</b>
		</field>
		<field-exclude>
			<a>error</a>
			<b>error</b>
		</field-exclude>
		</mapping>
	 */

	
	/**
	 @Autowired
	private DozerBeanMapper mapper;

	@Test
	public void mapObjectMapWithErrorMessageTOPortalRespone_ValidSessionId_IdenticalOutput() {
		String errorMessage = "messyage";

		ObjectMap input = new ObjectMap();
		input.add("errorMessage", errorMessage);

		PortalResponse response = this.mapper.map(input, PortalResponse.class);
		assertThat(response.getErrorMessage(), is(equalTo(errorMessage)));
		assertThat(response.getError(), is(true));
		assertThat(response.getErrorCode(), is(nullValue()));
	}

	@Test
	public void mapObjectMapWithErrorMessageAndCodeTOPortalRespone_ValidSessionId_IdenticalOutput() {
		String errorMessage = "coded message";
		String errorCode = "911";

		ObjectMap input = new ObjectMap();
		input.add("errorMessage", errorMessage);
		input.add("errorCode", errorCode);

		PortalResponse response = this.mapper.map(input, PortalResponse.class);
		assertThat(response.getErrorMessage(), is(equalTo(errorMessage)));
		assertThat(response.getError(), is(true));
		assertThat(response.getErrorCode(), is(equalTo(errorCode)));
	}

	@Test
	public void mapObjectMapWithNoErrorMessageTOPortalRespone_ValidSessionId_IdenticalOutput() {
		ObjectMap input = new ObjectMap();

		PortalResponse response = this.mapper.map(input, PortalResponse.class);
		assertThat(response.getError(), is(false));
		assertThat(response.getErrorMessage(), is(nullValue()));
		assertThat(response.getErrorCode(), is(nullValue()));
	}
	 */
}
