package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "e-access")
public interface AccessRestClient {
    @RequestMapping(path = "/ein/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateEin(@RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/ein/addRelatedProcess", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postAddRelatedProcess(@RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateFs(@RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cn/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateCn(@RequestBody JsonNode jsonData) throws Exception;
}
