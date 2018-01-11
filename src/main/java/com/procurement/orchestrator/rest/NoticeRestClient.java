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
    ResponseEntity<ResponseDto> createEin(@RequestParam("cpid") final String cpid,
                                          @RequestParam("stage") final String stage,
                                          @RequestParam("operation") final String operation,
                                          @RequestBody final JsonNode data) throws Exception;

    @RequestMapping(path = "/release/fs", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createFs(@RequestParam("cpid") final String cpid,
                                          @RequestParam("stage") final String stage,
                                          @RequestParam("operation") final String operation,
                                          @RequestBody final JsonNode data) throws Exception;

    @RequestMapping(path = "/release/cn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCn(@RequestParam("cpid") final String cpid,
                                         @RequestParam("stage") final String stage,
                                         @RequestParam("operation") final String operation,
                                         @RequestParam("startDate") final String releaseDate,
                                         @RequestBody final JsonNode data) throws Exception;

}
