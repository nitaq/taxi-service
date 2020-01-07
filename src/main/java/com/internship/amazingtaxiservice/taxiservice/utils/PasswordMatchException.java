package com.internship.amazingtaxiservice.taxiservice.utils;

public class PasswordMatchException extends BadRequestAlertException {

    public PasswordMatchException() {
        super("Passwords do not match!");
    }
}
