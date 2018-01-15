package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-access")
public interface AccessRestClient {
    @RequestMapping(path = "/ein/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateEin(@RequestParam("country") String country,
                                              @RequestParam("pmd") String pmd,
                                              @RequestParam("stage") String stage,
                                              @RequestParam("owner") String owner,
                                              @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/ein/updateAmountByFs", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateAmountByFs(@RequestParam("cpid") String cpid) throws Exception;

    @RequestMapping(path = "/fs/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateFs(@RequestParam("country") String country,
                                             @RequestParam("pmd") String pmd,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("owner") String owner,
                                             @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/cn/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateCn(@RequestParam("country") String country,
                                             @RequestParam("pmd") String pmd,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("owner") String owner,
                                             @RequestBody JsonNode jsonData) throws Exception;
}
