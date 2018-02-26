package com.procurement.orchestrator.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.utils.JsonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestJson {

    private final JsonUtil jsonUtil = new JsonUtil(new ObjectMapper());

    @Test
    public void getJsonNodeTest() {
        final String resource = jsonUtil.getResource("json/lots.json");
        final JsonNode lotsData = jsonUtil.toJsonNode(resource);
        JsonNode jsonData = jsonUtil.createObjectNode();
        ((ObjectNode) jsonData).replace("lots", lotsData.get("lots"));
        assertNotNull(jsonData.get("lots"));
    }

}