package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.command.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "e-mdm")
public interface MdmRestClient {

    @RequestMapping(method = RequestMethod.POST, value = "/command")
    ResponseEntity<ResponseDto> execute(@RequestBody JsonNode commandMessage) throws Exception;

    @RequestMapping(method = RequestMethod.GET, path = "/addresses/countries/{countryId}")
    ResponseEntity<String> getCountry(
        @PathVariable("countryId") String countryId,
        @RequestParam("lang") String lang
    );

    @RequestMapping(method = RequestMethod.GET, path = "/addresses/countries/{countryId}/regions/{regionId}")
    ResponseEntity<String> getRegion(
        @PathVariable("countryId") String countryId,
        @PathVariable("regionId") String regionId,
        @RequestParam("lang") String lang
    );

    @RequestMapping(method = RequestMethod.GET, path = "/addresses/countries/{countryId}/regions/{regionId}/localities/{localityId}")
    ResponseEntity<String> getLocality(
        @PathVariable("countryId") String countryId,
        @PathVariable("regionId") String regionId,
        @PathVariable("localityId") String localityId,
        @RequestParam("lang") String lang
    );

    @RequestMapping(method = RequestMethod.GET, path = "/organization/schemes")
    ResponseEntity<String> getOrganizationSchemes(@RequestParam("country") String country);

    @RequestMapping(method = RequestMethod.GET, path = "/organization/scales")
    ResponseEntity<String> getOrganizationScales(@RequestParam("country") String country);
}
