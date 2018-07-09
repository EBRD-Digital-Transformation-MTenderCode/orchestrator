package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-storage")
public interface StorageRestClient {

    @RequestMapping(method = RequestMethod.POST, value = "/storage/publish")
    ResponseEntity<ResponseDto> setPublishDate(@RequestParam("datePublished") String datePublished,
                                               @RequestBody JsonNode jsonData) throws Exception;
}
