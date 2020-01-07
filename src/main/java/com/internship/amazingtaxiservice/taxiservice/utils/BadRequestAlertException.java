package com.internship.amazingtaxiservice.taxiservice.utils;

import java.util.HashMap;
import java.util.Map;


public class BadRequestAlertException extends RuntimeException {

    private final String defaultMessage;


    public BadRequestAlertException(String defaultMessage) {
        super(defaultMessage);
        this.defaultMessage = defaultMessage;
    }


    private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "error." + errorKey);
        parameters.put("params", entityName);
        return parameters;
    }
}