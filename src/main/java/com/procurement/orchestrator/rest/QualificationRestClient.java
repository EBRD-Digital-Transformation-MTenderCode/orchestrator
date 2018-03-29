package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.response.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-qualification")
public interface QualificationRestClient {

    @RequestMapping(path = "/qualification", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createAwards(@RequestParam(value = "cpId") String cpId,
                                             @RequestParam(value = "stage") String stage,
                                             @RequestParam(value = "owner") String owner,
                                             @RequestParam(value = "country") String country,
                                             @RequestParam(value = "pmd") String pmd,
                                             @RequestParam(value = "startDate") String startDate,
                                             @RequestBody JsonNode dataDto) throws Exception;

    @RequestMapping(path = "/qualification", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateAward(@RequestParam(value = "cpId") String cpId,
                                            @RequestParam(value = "stage") String stage,
                                            @RequestParam(value = "token") String token,
                                            @RequestParam(value = "owner") String owner,
                                            @RequestBody JsonNode dataDto) throws Exception;

    @RequestMapping(path = "/qualification/period", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> checkAwarded(@RequestParam(value = "cpId") String cpId,
                                             @RequestParam(value = "stage") String stage,
                                             @RequestParam(value = "country") String country,
                                             @RequestParam(value = "pmd") String pmd,
                                             @RequestParam(value = "endPeriod") String endPeriod) throws Exception;

    @RequestMapping(path = "/qualification", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getAwards(@RequestParam("ocId") String ocId,
                                          @RequestParam("stage") String stage,
                                          @RequestParam(value = "country") String country,
                                          @RequestParam(value = "pmd") String pmd) throws Exception;


}
