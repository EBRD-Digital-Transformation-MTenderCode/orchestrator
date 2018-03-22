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
    ResponseEntity<ResponseDto> createCn(@RequestParam("owner") String owner,
                                         @RequestParam("date") final String dateTime,
                                         @RequestBody final JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cn", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateCn(@RequestParam("cpId") final String cpId,
                                         @RequestParam("token") final String token,
                                         @RequestParam("owner") final String owner,
                                         @RequestBody final JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/tender/updateStatus", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateTenderStatus(@RequestParam("cpId") final String cpId,
                                                   @RequestParam("status") final String status) throws Exception;

    @RequestMapping(path = "/tender/updateStatusDetails", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateTenderStatusDetails(@RequestParam("cpId") final String cpId,
                                                          @RequestParam("statusDetails") final String statusDetails)
            throws Exception;

    @RequestMapping(path = "/tender/setSuspended", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> setSuspended(@RequestParam("cpId") final String cpId,
                                             @RequestParam("suspended") final Boolean suspended) throws Exception;

    @RequestMapping(path = "/lots", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getLots(@RequestParam("cpId") final String cpId,
                                        @RequestParam("status") final String status) throws Exception;

    @RequestMapping(path = "/lots/updateStatus", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateLotsStatus(@RequestParam("cpId") final String cpId,
                                                 @RequestParam("status") final String status,
                                                 @RequestBody final JsonNode lotsDto) throws Exception;

    @RequestMapping(path = "/lots/updateStatusDetails", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateLotsStatusDetails(@RequestParam("cpId") final String cpId,
                                                        @RequestParam("statusDetails") final String statusDetails,
                                                        @RequestBody final JsonNode lotsDto) throws Exception;

}
