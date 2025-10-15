package com.ra.base_spring_boot.exception;

public class HttpAccessDenied extends RuntimeException {
    public HttpAccessDenied(String message) {
        super(message);
    }
}
