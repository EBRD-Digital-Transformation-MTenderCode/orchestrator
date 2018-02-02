package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-notice")
public interface NoticeRestClient {

    @RequestMapping(path = "/release/ein", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createEin(@RequestParam("cpId") final String cpId,
                                          @RequestParam("stage") final String stage,
                                          @RequestBody final JsonNode data) throws Exception;

    @RequestMapping(path = "/release/ein", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateEin(@RequestParam("cpId") final String cpId,
                                          @RequestParam("stage") final String stage,
                                          @RequestBody final JsonNode data) throws Exception;

    @RequestMapping(path = "/release/fs", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createFs(@RequestParam("cpId") final String cpId,
                                         @RequestParam("stage") final String stage,
                                         @RequestBody final JsonNode data) throws Exception;

    @RequestMapping(path = "/release/fs", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateFs(@RequestParam("cpId") final String cpId,
                                         @RequestParam("ocId") final String ocId,
                                         @RequestParam("stage") final String stage,
                                         @RequestBody final JsonNode data) throws Exception;

    @RequestMapping(path = "/release/cn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCn(@RequestParam("cpId") final String cpId,
                                         @RequestParam("stage") final String stage,
                                         @RequestParam("startDate") final String releaseDate,
                                         @RequestBody final JsonNode data) throws Exception;


    @RequestMapping(path = "/release/enquiry", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createEnquiry(@RequestParam("cpId") final String cpId,
                                              @RequestParam("cpId") final String ocId,
                                              @RequestParam("cpId") final String stage,
                                              @RequestBody final JsonNode data) throws Exception;
}
