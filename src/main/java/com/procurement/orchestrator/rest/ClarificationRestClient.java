package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "e-clarification")
public interface ClarificationRestClient {

    @RequestMapping(method = RequestMethod.POST, value = "/command")
    ResponseEntity<ResponseDto> execute(@RequestBody JsonNode commandMessage) throws Exception;

}
