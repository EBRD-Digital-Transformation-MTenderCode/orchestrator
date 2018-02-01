package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-submission")
public interface SubmissionRestClient {
    @RequestMapping(path = "/period/check", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> checkPeriod(@RequestParam("country") String country,
                                            @RequestParam("pmd") String pmd,
                                            @RequestParam("stage") String stage,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate) throws Exception;

    @RequestMapping(path = "/period/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> savePeriod(@RequestParam("cpid") String cpid,
                                           @RequestParam("stage") String stage,
                                           @RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate) throws Exception;

    @RequestMapping(path = "/submission/bid", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createBid(@RequestParam("cpId") String cpId,
                                          @RequestParam("stage") String stage,
                                          @RequestParam("owner") String owner,
                                          @RequestBody JsonNode bidDto) throws Exception;

    @RequestMapping(path = "/submission/bid", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateBid(@RequestParam("cpId") String cpId,
                                          @RequestParam("stage") String stage,
                                          @RequestParam("token") String token,
                                          @RequestParam("owner") String owner,
                                          @RequestBody JsonNode bidDto) throws Exception;
}
