package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-contracting")
public interface ContractingRestClient {

    @RequestMapping(path = "/createCAN", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCan(@RequestParam(value = "identifier") String cpid,
                                          @RequestParam(value = "stage") String stage,
                                          @RequestParam(value = "owner") String owner,
                                          @RequestParam("date") String date,
                                          @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/createAC", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createAC(@RequestParam(value = "identifier") String cpid,
                                         @RequestParam(value = "stage") String stage,
                                         @RequestParam("date") String date,
                                         @RequestBody JsonNode jsonData) throws Exception;

}
