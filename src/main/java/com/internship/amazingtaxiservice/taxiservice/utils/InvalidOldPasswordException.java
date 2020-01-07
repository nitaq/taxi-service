package com.internship.amazingtaxiservice.taxiservice.utils;

public class InvalidOldPasswordException extends BadRequestAlertException {

    public InvalidOldPasswordException() {
        super("This password does not match!");
    }
}
