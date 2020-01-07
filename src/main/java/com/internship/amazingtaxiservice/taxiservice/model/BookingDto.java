package com.internship.amazingtaxiservice.taxiservice.model;

import lombok.*;

import java.util.Date;

@Data
public class BookingDto {

    private int user_id;
    private int taxi_id;
    private Date time;
    private String pickupLocation;
    private String destination;
    private boolean isReserved;
}