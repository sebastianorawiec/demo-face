package com.azure.face.demo.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {

    private final String headerName;

    private final String headerValue;

    public HeaderRequestInterceptor(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // add application/json header to all requests where we have no explicit set application/octet-stream
        if (request.getHeaders().getContentType() !=null &&  !request.getHeaders().getContentType().
                includes(MediaType.APPLICATION_OCTET_STREAM)){

            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }

        request.getHeaders().set(headerName, headerValue);
        return execution.execute(request, body);
    }





}