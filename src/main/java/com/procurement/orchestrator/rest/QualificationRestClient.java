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
    ResponseEntity<ResponseDto> createAwards(@RequestParam(value = "cpId") final String cpId,
                                             @RequestParam(value = "stage") final String stage,
                                             @RequestParam(value = "owner") final String owner,
                                             @RequestParam(value = "country") final String country,
                                             @RequestParam(value = "pmd") final String pmd,
                                             @RequestParam(value = "startDate") final String startDate,
                                             @RequestBody final JsonNode dataDto) throws Exception;

    @RequestMapping(path = "/qualification", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateAward(@RequestParam(value = "cpId") final String cpId,
                                            @RequestParam(value = "stage") final String stage,
                                            @RequestParam(value = "token") final String token,
                                            @RequestParam(value = "owner") final String owner,
                                            @RequestBody final JsonNode dataDto) throws Exception;

    @RequestMapping(path = "/qualification/period", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> checkAwarded(@RequestParam(value = "cpId") final String cpId,
                                             @RequestParam(value = "stage") final String stage,
                                             @RequestParam(value = "country") final String country,
                                             @RequestParam(value = "pmd") final String pmd,
                                             @RequestParam(value = "endPeriod") final String endPeriod) throws Exception;

    @RequestMapping(path = "/qualification", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getAwards(@RequestParam("ocId") final String ocId,
                                          @RequestParam("stage") final String stage,
                                          @RequestParam(value = "country") final String country,
                                          @RequestParam(value = "pmd") final String pmd) throws Exception;


}
