package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.cn.RequestSubmissionPeriodDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "e-submission")
public interface SubmissionRestClient {
    @RequestMapping(path = "/period/check", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postCheckPeriod(@RequestBody RequestSubmissionPeriodDto dto) throws Exception;

    @RequestMapping(path = "/period/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postSavePeriod(@RequestBody RequestSubmissionPeriodDto dto) throws Exception;
}
