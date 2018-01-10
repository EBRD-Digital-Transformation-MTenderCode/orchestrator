package com.procurement.orchestrator.rest;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-storage")
public interface StorageRestClient {

    @RequestMapping(path = "/datePublished", method = RequestMethod.POST)
    ResponseEntity<String> setPublishDate(@RequestParam(value = "fileId") final String fileId,
                                          @RequestParam(value = "datePublished") final String datePublished)
            throws Exception;
}
