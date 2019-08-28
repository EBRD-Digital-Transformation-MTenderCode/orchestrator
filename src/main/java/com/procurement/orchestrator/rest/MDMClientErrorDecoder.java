package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.exception.MDMException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class MDMClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(final String methodKey, final Response response) {
        return MDMException.create(response);
    }
}
