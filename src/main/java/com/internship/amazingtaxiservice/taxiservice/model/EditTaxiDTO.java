package com.internship.amazingtaxiservice.taxiservice.model;

import lombok.*;

@Data
public class EditTaxiDTO {

    private int taxi_id;
    private String name;
    private int number;
    private int status_id;
}
