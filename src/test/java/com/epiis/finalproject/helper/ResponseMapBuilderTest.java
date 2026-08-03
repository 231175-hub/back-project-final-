package com.epiis.finalproject.helper;

import com.epiis.finalproject.dto.response.role.ResponseRoleInsert;
import com.epiis.finalproject.generic.ResponseGeneric;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResponseMapBuilderTest {

    @Test
    void testBuildDataMapContainsMessageAndData() {
        ResponseGeneric response = new ResponseRoleInsert();
        List<String> data = List.of("item1", "item2");

        Map<String, Object> result = ResponseMapBuilder.buildDataMap(response, "Test message", data);

        assertNotNull(result);
        assertTrue(result.containsKey("message"));
        assertTrue(result.containsKey("data"));
    }

    @Test
    void testBuildDataMapSetsSuccessOnResponse() {
        ResponseGeneric response = new ResponseRoleInsert();

        ResponseMapBuilder.buildDataMap(response, "OK", null);

        assertEquals("success", response.getType());
    }

    @Test
    void testBuildDataMapAddsMessageToResponse() {
        ResponseGeneric response = new ResponseRoleInsert();

        ResponseMapBuilder.buildDataMap(response, "Hello", null);

        assertEquals(1, response.getListMessage().size());
        assertEquals("Hello", response.getListMessage().get(0));
    }

    @Test
    void testBuildDataMapDataIsPassedThrough() {
        ResponseGeneric response = new ResponseRoleInsert();
        String payload = "my-payload";

        Map<String, Object> result = ResponseMapBuilder.buildDataMap(response, "msg", payload);

        assertEquals(payload, result.get("data"));
    }

    @Test
    void testBuildDataMapMessageKeyIsTheResponseObject() {
        ResponseGeneric response = new ResponseRoleInsert();

        Map<String, Object> result = ResponseMapBuilder.buildDataMap(response, "msg", null);

        assertSame(response, result.get("message"));
    }
}
