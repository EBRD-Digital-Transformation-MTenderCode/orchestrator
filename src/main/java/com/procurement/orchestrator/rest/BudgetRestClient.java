package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-budget")
public interface BudgetRestClient {

    @RequestMapping(path = "/ei", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createEi(@RequestParam("owner") final String owner,
                                          @RequestBody final JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/ei", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateEi(@RequestParam("cpId") final String cpId,
                                          @RequestParam("owner") final String owner,
                                          @RequestParam("token") final String token,
                                          @RequestBody final JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createFs(@RequestParam("cpId") final String cpId,
                                         @RequestParam("owner") final String owner,
                                         @RequestBody final JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateFs(@RequestParam("cpId") final String cpId,
                                         @RequestParam("token") final String token,
                                         @RequestParam("owner") final String owner,
                                         @RequestBody final JsonNode jsonData) throws Exception;

}
