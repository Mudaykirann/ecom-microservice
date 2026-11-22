package com.ecom.order_service.exception;

import feign.Response;
import feign.codec.ErrorDecoder;


public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == 404 && response.request().url().contains("inventory")) {
            return new RuntimeException("Product not Found");
        }
        return new RuntimeException("Generic Exception" + response.status());
    }
}
