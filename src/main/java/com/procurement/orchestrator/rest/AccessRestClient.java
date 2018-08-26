package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.CommandMessage;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-access")
public interface AccessRestClient {

    @RequestMapping(method = RequestMethod.POST, value = "/command")
    ResponseEntity<ResponseDto> execute(@RequestBody CommandMessage commandMessage) throws Exception;

    @RequestMapping(path = "/cn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCn(@RequestParam("stage") String stage,
                                         @RequestParam("country") String country,
                                         @RequestParam("pmd") String pmd,
                                         @RequestParam("owner") String owner,
                                         @RequestParam("date") String dateTime,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cn", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateCn(@RequestParam("cpid") String cpId,
                                         @RequestParam("stage") String stage,
                                         @RequestParam("owner") String owner,
                                         @RequestParam("token") String token,
                                         @RequestParam("date") String dateTime,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/pin", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createPin(@RequestParam("stage") String stage,
                                          @RequestParam("country") String country,
                                          @RequestParam("pmd") String pmd,
                                          @RequestParam("owner") String owner,
                                          @RequestParam("date") String dateTime,
                                          @RequestBody JsonNode jsonData) throws Exception;


    @RequestMapping(path = "/pn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createPn(@RequestParam("stage") String stage,
                                         @RequestParam("country") String country,
                                         @RequestParam("pmd") String pmd,
                                         @RequestParam("owner") String owner,
                                         @RequestParam("date") String dateTime,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/pn", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updatePn(@RequestParam("cpid") String cpId,
                                         @RequestParam("stage") String stage,
                                         @RequestParam("owner") String owner,
                                         @RequestParam("token") String token,
                                         @RequestParam("date") String dateTime,
                                         @RequestBody JsonNode jsonData) throws Exception;


    @RequestMapping(path = "/pinOnPn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createPinOnPn(@RequestParam("cpid") String cpId,
                                              @RequestParam("token") String token,
                                              @RequestParam("country") String country,
                                              @RequestParam("pmd") String pmd,
                                              @RequestParam("owner") String owner,
                                              @RequestParam("stage") String stage,
                                              @RequestParam("previousStage") String previousStage,
                                              @RequestParam("date") String dateTime,
                                              @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cnOnPn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCnOnPn(@RequestParam("cpid") String cpId,
                                             @RequestParam("previousStage") String previousStage,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("country") String country,
                                             @RequestParam("pmd") String pmd,
                                             @RequestParam("owner") String owner,
                                             @RequestParam("token") String token,
                                             @RequestParam("date") String dateTime,
                                             @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cnOnPin", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCnOnPin(@RequestParam("cpid") String cpId,
                                              @RequestParam("previousStage") String previousStage,
                                              @RequestParam("stage") String stage,
                                              @RequestParam("country") String country,
                                              @RequestParam("pmd") String pmd,
                                              @RequestParam("owner") String owner,
                                              @RequestParam("token") String token,
                                              @RequestParam("date") String dateTime,
                                              @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/tender/updateStatus", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateTenderStatus(@RequestParam("cpid") String cpId,
                                                   @RequestParam("stage") String stage,
                                                   @RequestParam("status") String status) throws Exception;

    @RequestMapping(path = "/tender/updateStatusDetails", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateTenderStatusDetails(@RequestParam("cpid") String cpId,
                                                          @RequestParam("stage") String stage,
                                                          @RequestParam("statusDetails") String statusDetails) throws Exception;

    @RequestMapping(path = "/tender/setSuspended", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> setSuspended(@RequestParam("cpid") String cpId,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("suspended") Boolean suspended) throws Exception;

    @RequestMapping(path = "/tender/setUnsuccessful", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> setUnsuccessful(@RequestParam("cpid") String cpId,
                                                @RequestParam("stage") String stage) throws Exception;

    @RequestMapping(path = "/lots", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getLots(@RequestParam("cpid") String cpId,
                                        @RequestParam("stage") String stage,
                                        @RequestParam("status") String status) throws Exception;

    @RequestMapping(path = "/lots/updateLots", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateLots(@RequestParam("cpid") String cpId,
                                           @RequestParam("stage") String stage,
                                           @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/lots/updateLotsEv", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateLotsEv(@RequestParam("cpid") String cpId,
                                             @RequestParam("stage") String stage,
                                             @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/lots/updateStatusDetails", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateLotsStatusDetails(@RequestParam("cpid") String cpId,
                                                        @RequestParam("stage") String stage,
                                                        @RequestParam("statusDetails") String statusDetails,
                                                        @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/lots/updateStatusDetailsById", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateStatusDetailsById(@RequestParam("cpid") String cpId,
                                                        @RequestParam("stage") String stage,
                                                        @RequestParam("lotAwarded") Boolean lotAwarded,
                                                        @RequestParam("lotId") String lotId) throws Exception;

    @RequestMapping(path = "/lots/checkStatusDetails", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> checkStatusDetails(@RequestParam("cpid") String cpId,
                                                   @RequestParam("stage") String stage) throws Exception;

    @RequestMapping(path = "/lots/checkStatus", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> checkStatus(@RequestParam("cpid") String cpId,
                                            @RequestParam("stage") String stage,
                                            @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/newStage", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> startNewStage(@RequestParam("cpid") String cpId,
                                              @RequestParam("token") String token,
                                              @RequestParam("previousStage") String previousStage,
                                              @RequestParam("stage") String newStage,
                                              @RequestParam("owner") String owner) throws Exception;


    @RequestMapping(path = "tender/prepareCancellation", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> prepareCancellation(@RequestParam("cpid") String cpId,
                                                    @RequestParam("stage") String stage,
                                                    @RequestParam("owner") String owner,
                                                    @RequestParam("token") String token,
                                                    @RequestParam("operationType") String operationType) throws Exception;

    @RequestMapping(path = "tender/tenderCancellation", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> tenderCancellation(@RequestParam("cpid") String cpId,
                                                   @RequestParam("stage") String stage,
                                                   @RequestParam("owner") String owner,
                                                   @RequestParam("token") String token,
                                                   @RequestParam("operationType") String operationType) throws Exception;


}
