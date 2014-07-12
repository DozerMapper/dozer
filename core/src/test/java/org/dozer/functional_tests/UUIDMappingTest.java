package org.dozer.functional_tests;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class UUIDMappingTest extends AbstractFunctionalTest {

    @Test
    public void testCopyUUIDByReferenceAsDefault() {
        UUID uuidToMap = UUID.randomUUID();
        UUID mappedUUID = mapper.map(uuidToMap, UUID.class);

        assertTrue(mappedUUID == uuidToMap);
    }

}
