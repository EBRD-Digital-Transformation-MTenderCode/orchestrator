package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.RequestDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notice")
public interface NoticeRestClient {
    @RequestMapping(path = "/release/saveTwineRecordRelease", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postTwineRelease(@RequestBody RequestDto data) throws Exception;

    @RequestMapping(path = "/release/saveRecordRelease", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postRelease(@RequestParam("cpId") String cpId, @RequestBody RequestDto data) throws Exception;
}
