package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-evaluation")
public interface EvaluationRestClient {

    @RequestMapping(path = "/evaluation", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createAwards(@RequestParam("cpid") String cpId,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("owner") String owner,
                                             @RequestParam("country") String country,
                                             @RequestParam("pmd") String pmd,
                                             @RequestParam("awardCriteria") String awardCriteria,
                                             @RequestParam("date") String dateTime,
                                             @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/evaluation", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateAward(@RequestParam("cpid") String cpId,
                                            @RequestParam("stage") String stage,
                                            @RequestParam("token") String token,
                                            @RequestParam("awardId") String awardId,
                                            @RequestParam("owner") String owner,
                                            @RequestParam(value = "date") String dateTime,
                                            @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/evaluation", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getAwards(@RequestParam("cpid") String cpId,
                                          @RequestParam("stage") String stage,
                                          @RequestParam("country") String country,
                                          @RequestParam("pmd") String pmd) throws Exception;

    @RequestMapping(path = "/evaluation/endAwardPeriod", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> endAwardPeriod(@RequestParam("cpid") String cpId,
                                               @RequestParam("stage") String stage,
                                               @RequestParam("country") String country,
                                               @RequestParam("pmd") String pmd,
                                               @RequestParam("endPeriod") String endPeriod) throws Exception;


}
