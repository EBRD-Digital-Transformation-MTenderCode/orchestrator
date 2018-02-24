package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import javax.validation.Valid;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-submission")
public interface SubmissionRestClient {

    @RequestMapping(path = "/period/check", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> checkPeriod(@RequestParam("cpId") final String cpId,
                                            @RequestParam("country") String country,
                                            @RequestParam("pmd") String pmd,
                                            @RequestParam("stage") String stage,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate) throws Exception;

    @RequestMapping(path = "/period/validation", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> periodValidation(@RequestParam("country") final String country,
                                                 @RequestParam("pmd") final String pmd,
                                                 @RequestParam("startDate") final String startDate,
                                                 @RequestParam("endDate")final String endDate) throws Exception;



    @RequestMapping(path = "/period/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> savePeriod(@RequestParam("cpId") String cpId,
                                           @RequestParam("stage") String stage,
                                           @RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate) throws Exception;

    @RequestMapping(path = "/period/new", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> saveNewPeriod(@RequestParam("cpId") final String cpId,
                                              @RequestParam("stage") final String stage,
                                              @RequestParam("country") final String country,
                                              @RequestParam("pmd") final String pmd,
                                              @RequestParam("startDate") final String startDate) throws Exception;

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

    @RequestMapping(path = "/submission/copyBids", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> copyBids(@RequestParam("cpId") final String cpId,
                                         @RequestParam("stage") final String stage,
                                         @RequestParam("previousStage") final String previousStage,
                                         @Valid @RequestBody final JsonNode lots) throws Exception;

    @RequestMapping(path = "/submission/bids", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getBids(@RequestParam("cpId") final String cpId,
                                        @RequestParam("stage") final String stage,
                                        @RequestParam("country") final String country,
                                        @RequestParam("pmd") final String pmd) throws Exception;

    @RequestMapping(path = "/submission/updateStatus", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateStatus(@RequestParam("cpId") final String cpId,
                                             @RequestParam("stage") final String stage,
                                             @RequestParam("country") final String country,
                                             @RequestParam("pmd") final String pmd,
                                             @RequestBody final JsonNode lots) throws Exception;

    @RequestMapping(path = "/submission/updateStatusDetail", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateStatusDetail(@RequestParam("cpId") final String cpId,
                                                   @RequestParam("stage") final String stage,
                                                   @RequestParam("bidId") final String bidId,
                                                   @RequestParam("awardStatus") final String awardStatus)
            throws Exception;

    @RequestMapping(path = "/submission/setFinalStatuses", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> setFinalStatuses(@RequestParam("cpId") final String cpId,
                                                 @RequestParam("stage") final String stage) throws Exception;
}
