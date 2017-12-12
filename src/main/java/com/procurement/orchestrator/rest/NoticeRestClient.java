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
    @RequestMapping(path = "/release/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postData(@RequestParam("cpid") String cpid,
                                         @RequestParam("ocid") String ocid,
                                         @RequestParam("tag") String tag,
                                         @RequestParam("initiationType") String initiationType,
                                         @RequestParam("language") String language,
                                         @RequestBody RequestDto data) throws Exception;
}
