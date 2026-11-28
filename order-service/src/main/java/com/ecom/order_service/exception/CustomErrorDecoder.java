package com.ecom.order_service.exception;

import feign.Response;
import feign.FeignException;
import feign.codec.ErrorDecoder;


public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        return FeignException.errorStatus(s, response);
    }
}
