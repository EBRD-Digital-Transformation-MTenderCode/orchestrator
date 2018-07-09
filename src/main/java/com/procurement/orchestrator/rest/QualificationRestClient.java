package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-qualification")
public interface QualificationRestClient {

    @RequestMapping(path = "/qualification", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createAwards(@RequestParam("identifier") String cpId,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("owner") String owner,
                                             @RequestParam("country") String country,
                                             @RequestParam("pmd") String pmd,
                                             @RequestParam("startDate") String startDate,
                                             @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/qualification", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateAward(@RequestParam("identifier") String cpId,
                                            @RequestParam("stage") String stage,
                                            @RequestParam("token") String token,
                                            @RequestParam("owner") String owner,
                                            @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/qualification/checkAwarded", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> checkAwarded(@RequestParam("identifier") String cpId,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("country") String country,
                                             @RequestParam("pmd") String pmd) throws Exception;

    @RequestMapping(path = "/qualification/endAwardPeriod", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> endAwardPeriod(@RequestParam("identifier") String cpId,
                                               @RequestParam("stage") String stage,
                                               @RequestParam("country") String country,
                                               @RequestParam("pmd") String pmd,
                                               @RequestParam("endPeriod") String endPeriod) throws Exception;


}
