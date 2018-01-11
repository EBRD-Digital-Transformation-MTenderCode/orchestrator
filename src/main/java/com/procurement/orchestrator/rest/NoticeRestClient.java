package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.RequestNoticeDto;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import java.time.LocalDateTime;
import javax.validation.Valid;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-notice")
public interface NoticeRestClient {

    @RequestMapping(path = "/release/saveRecordRelease", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postRelease(@RequestBody RequestNoticeDto data) throws Exception;

    @RequestMapping(path = "/release/saveRecordRelease", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createCn(@RequestParam("cpid") final String cpid,
                                @RequestParam("stage") final String stage,
                                @RequestParam("operation") final String operation,
                                @RequestParam("startDate") final String releaseDate,
                                @RequestBody final JsonNode data) throws Exception;
}
