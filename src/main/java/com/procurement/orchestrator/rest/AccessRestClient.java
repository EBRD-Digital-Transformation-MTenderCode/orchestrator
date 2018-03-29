package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.response.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-access")
public interface AccessRestClient {

    @RequestMapping(path = "/cn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCn(@RequestParam("stage") String stage,
                                         @RequestParam("country") String country,
                                         @RequestParam("owner") String owner,
                                         @RequestParam("date") String dateTime,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cn", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateCn(@RequestParam("identifier") String cpId,
                                         @RequestParam("token") String token,
                                         @RequestParam("owner") String owner,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/tender/updateStatus", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateTenderStatus(@RequestParam("identifier") String cpId,
                                                   @RequestParam("status") String status) throws Exception;

    @RequestMapping(path = "/tender/updateStatusDetails", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateTenderStatusDetails(@RequestParam("identifier") String cpId,
                                                          @RequestParam("statusDetails") String statusDetails)
            throws Exception;

    @RequestMapping(path = "/tender/setSuspended", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> setSuspended(@RequestParam("identifier") String cpId,
                                             @RequestParam("suspended") Boolean suspended) throws Exception;

    @RequestMapping(path = "/lots", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getLots(@RequestParam("identifier") String cpId,
                                        @RequestParam("status") String status) throws Exception;

    @RequestMapping(path = "/lots/updateStatus", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateLotsStatus(@RequestParam("identifier") String cpId,
                                                 @RequestParam("status") String status,
                                                 @RequestBody JsonNode lotsDto) throws Exception;

    @RequestMapping(path = "/lots/updateStatusDetails", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateLotsStatusDetails(@RequestParam("identifier") String cpId,
                                                        @RequestParam("statusDetails") String statusDetails,
                                                        @RequestBody JsonNode lotsDto) throws Exception;

}
