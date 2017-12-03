package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "access")
public interface AccessRestClient {
    @RequestMapping(path = "/saveEIN", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postData(@RequestBody RequestDto requestDto) throws Exception;
}
