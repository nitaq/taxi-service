package com.internship.amazingtaxiservice.taxiservice.utils;

public class InvalidPasswordOrUsernameException extends BadRequestAlertException {

    public InvalidPasswordOrUsernameException() {
        super("Username or Password is incorrect!");
    }
}
