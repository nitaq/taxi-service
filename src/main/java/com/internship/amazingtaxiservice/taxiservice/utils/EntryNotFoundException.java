package com.internship.amazingtaxiservice.taxiservice.utils;

public class EntryNotFoundException extends BadRequestAlertException {

    public EntryNotFoundException(String entry) {
        super(entry + " not found");
    }
}