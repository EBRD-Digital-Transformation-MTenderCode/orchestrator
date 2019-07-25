package com.procurement.orchestrator.rest;

import com.procurement.orchestrator.exception.MDMException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MDMClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(final String methodKey, final Response response) {
        try {
            final String body = IOUtils.toString(response.body().asInputStream(), UTF_8);
            return new MDMException(body);
        } catch (Exception exception) {
            return exception;
        }
    }
}
