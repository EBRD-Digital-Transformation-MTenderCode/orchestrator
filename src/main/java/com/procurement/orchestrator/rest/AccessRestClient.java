package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.ResponseDto;
import java.util.Map;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "access")
public interface AccessRestClient {
    @RequestMapping(path = "/ein/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateEin(@RequestBody Map<String, String> jsonData) throws Exception;

    @RequestMapping(path = "/fs/create", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCreateFs(@RequestBody Map<String, String> jsonData) throws Exception;
}
