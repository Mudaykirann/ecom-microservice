package com.ecom.order_service.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public class GenericExceptions extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;

    public GenericExceptions(HttpStatusCode statusCode, HttpHeaders headers) {
        this.statusCode = statusCode;
        this.headers = headers;
    }
}
