package com.internship.amazingtaxiservice.taxiservice.utils;

public class UserDoesNotExistException extends BadRequestAlertException {

    public UserDoesNotExistException() {
        super("User does not exist");
    }
}
