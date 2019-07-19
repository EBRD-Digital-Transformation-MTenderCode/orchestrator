package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import feign.Param;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "e-mdm")
public interface MdmRestClient {

    @RequestMapping(method = RequestMethod.POST, value = "/command")
    ResponseEntity<ResponseDto> execute(@RequestBody JsonNode commandMessage) throws Exception;

    @GetMapping(path = "/addresses/countries/{countryId}", params = "lang")
    ResponseEntity<String> getCountry(
        @PathVariable("countryId") String countryId,
        @Param("lang") String lang
    ) throws Exception;

    @GetMapping(path = "/addresses/countries/{countryId}/regions/{regionId}", params = "lang")
    ResponseEntity<String> getRegion(
        @PathVariable("countryId") String countryId,
        @PathVariable("regionId") String regionId,
        @Param("lang") String lang
    ) throws Exception;

    @GetMapping(path = "/addresses/countries/{countryId}/regions/{regionId}/localities/{localityId}", params = "lang")
    ResponseEntity<String> getLocality(
        @PathVariable("countryId") String countryId,
        @PathVariable("regionId") String regionId,
        @PathVariable("localityId") String localityId,
        @Param("lang") String lang
    ) throws Exception;
}
