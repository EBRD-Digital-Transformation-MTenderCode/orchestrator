package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.response.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-contracting")
public interface ContractingRestClient {

    @RequestMapping(path = "/can", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCan(@RequestParam("stage") String stage,
                                         @RequestParam("country") String country,
                                         @RequestParam("pmd") String pmd,
                                         @RequestParam("owner") String owner,
                                         @RequestParam("date") String dateTime,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/awardedContract", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createAwardedContract(@RequestParam("stage") String stage,
                                                      @RequestParam("country") String country,
                                                      @RequestParam("pmd") String pmd,
                                                      @RequestParam("owner") String owner,
                                                      @RequestParam("date") String dateTime,
                                                      @RequestBody JsonNode jsonData) throws Exception;


}
