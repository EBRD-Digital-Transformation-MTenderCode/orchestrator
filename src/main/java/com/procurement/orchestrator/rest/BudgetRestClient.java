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
    ResponseEntity<ResponseDto> createEi(@RequestParam("owner") String owner,
                                         @RequestParam("country") String country,
                                         @RequestParam("date") String dateTime,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/ei", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateEi(@RequestParam("cpid") String cpId,
                                         @RequestParam("owner") String owner,
                                         @RequestParam("token") String token,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createFs(@RequestParam("cpid") String cpId,
                                         @RequestParam("owner") String owner,
                                         @RequestParam("date") String dateTime,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateFs(@RequestParam("cpid") String cpId,
                                         @RequestParam("ocid") String ocId,
                                         @RequestParam("token") String token,
                                         @RequestParam("owner") String owner,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs/check", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> checkFs(@RequestBody JsonNode jsonData) throws Exception;

}
