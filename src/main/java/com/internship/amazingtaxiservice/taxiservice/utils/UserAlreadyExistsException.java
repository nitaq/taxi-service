package com.internship.amazingtaxiservice.taxiservice.utils;

public class UserAlreadyExistsException extends BadRequestAlertException {

    public UserAlreadyExistsException() {
        super("This user already exists");
    }
}
