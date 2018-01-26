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

    @RequestMapping(path = "/ein", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createEin(@RequestParam("owner") String owner,
                                          @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/ein", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateEin(@RequestParam("owner") final String owner,
                                          @RequestParam("identifier") final String identifier,
                                          @RequestParam("token") final String token,
                                          @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createFs(@RequestParam("owner") String owner,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/fs", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateFs(@RequestParam("owner") final String owner,
                                         @RequestParam("identifier") final String identifier,
                                         @RequestParam("token") final String token,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCn(@RequestParam("owner") String owner,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cn", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateCn(@RequestParam("owner") final String owner,
                                         @RequestParam("identifier") final String identifier,
                                         @RequestParam("token") final String token,
                                         @RequestBody JsonNode jsonData) throws Exception;
}
