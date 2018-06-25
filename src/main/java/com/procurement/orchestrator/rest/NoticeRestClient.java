package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.response.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-notice")
public interface NoticeRestClient {

    @RequestMapping(path = "/release", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createRelease(@RequestParam("cpId") String cpId,
                                              @RequestParam("ocId") String ocId,
                                              @RequestParam("stage") String newStage,
                                              @RequestParam("previousStage") String previousStage,
                                              @RequestParam("operation") String operation,
                                              @RequestParam("phase") String phase,
                                              @RequestParam("releaseDate") String releaseDate,
                                              @RequestBody JsonNode jsonData) throws Exception;


}
