package com.internship.amazingtaxiservice.taxiservice.utils;

public class LoginAlreadyUsedException extends BadRequestAlertException {

    public LoginAlreadyUsedException() {
        super("This user is already in use");
    }
}