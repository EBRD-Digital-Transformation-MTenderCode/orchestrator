package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.domain.dto.ResponseDto;
import java.time.LocalDateTime;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-clarification")
public interface ClarificationRestClient {

    @RequestMapping(path = "/period/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> postSavePeriod(@RequestParam("cpid") final String cpid,
                                               @RequestParam("country") final String country,
                                               @RequestParam("pmd") final String pmd,
                                               @RequestParam("stage") final String stage,
                                               @RequestParam("owner") final String owner,
                                               @RequestParam("startDate") final String startDate,
                                               @RequestParam("endDate") final String endDate) throws Exception;
}
