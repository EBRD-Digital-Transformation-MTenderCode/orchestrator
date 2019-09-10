package com.procurement.orchestrator.exception;

import feign.Request;
import feign.Response;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
public class MDMException extends RuntimeException {
    private final String responseBody;

    public static MDMException create(final Response response) {
        final Request request = response.request();
        final String requestMethod = request.method().toUpperCase();
        final String requestUrl = request.url();
        final String requestBody = getRequestBody(request);
        final int responseStatus = response.status();
        final String responseBody = getResponseBody(response.body());

        final String message = "Request[" + requestMethod + " " + requestUrl + ", body: '" + requestBody + "'], Response[Status: '" + responseStatus + "', body: '" + responseBody + "']";
        return new MDMException(responseBody, message);
    }

    private static String getRequestBody(final Request request) {
        final byte[] body = request.body();
        return (request.body() == null || body.length == 0) ? "None" : new String(body);
    }

    private static String getResponseBody(final Response.Body body) {
        try {
            return IOUtils.toString(body.asInputStream(), UTF_8);
        } catch (Exception exception) {
            return "Error getting body of response.";
        }
    }

    public MDMException(final String responseBody, final String message) {
        super(message);
        this.responseBody = responseBody;
    }
}
