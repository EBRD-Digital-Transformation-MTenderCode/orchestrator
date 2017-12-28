package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.RequestNoticeDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "e-notice")
public interface NoticeRestClient {

    @RequestMapping(path = "/release/saveRecordRelease", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postRelease(@RequestBody RequestNoticeDto data) throws Exception;
}
