package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-access")
public interface AccessRestClient {
    @RequestMapping(path = "/ein/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateEin(@RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/ein/addRelatedProcess", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postAddRelatedProcess(@RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateFs(@RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cn/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateCn(@RequestParam("country") final String country,
                                             @RequestParam("pmd") final String pmd,
                                             @RequestParam("stage") final String stage,
                                             @RequestParam("owner") final String owner,
                                             @RequestBody final JsonNode jsonData) throws Exception;
}
