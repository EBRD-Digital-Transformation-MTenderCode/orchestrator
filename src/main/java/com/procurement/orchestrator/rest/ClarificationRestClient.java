package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-clarification")
public interface ClarificationRestClient {

    @RequestMapping(path = "/period/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> savePeriod(@RequestParam("cpId") final String cpId,
                                           @RequestParam("country") final String country,
                                           @RequestParam("pmd") final String pmd,
                                           @RequestParam("stage") final String stage,
                                           @RequestParam("owner") final String owner,
                                           @RequestParam("startDate") final String startDate,
                                           @RequestParam("endDate") final String endDate) throws Exception;

    @RequestMapping(path = "/enquiry", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createEnquiry(@RequestParam(value = "cpId") final String cpId,
                                              @RequestParam(value = "stage") final String stage,
                                              @RequestParam(value = "date") final String date,
                                              @RequestParam(value = "owner") final String owner,
                                              @RequestBody final JsonNode dataDto) throws Exception;

    @RequestMapping(path = "/enquiry", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateEnquiry(@RequestParam(value = "cpId") final String cpId,
                                              @RequestParam(value = "token") final String token,
                                              @RequestParam(value = "date") final String date,
                                              @RequestParam(value = "owner") final String owner,
                                              @RequestBody final JsonNode dataDto) throws Exception;

    @RequestMapping(path = "/enquiry", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> checkEnquiries(@RequestParam(value = "cpId") final String cpId,
                                               @RequestParam(value = "stage") final String stage) throws Exception;

}
