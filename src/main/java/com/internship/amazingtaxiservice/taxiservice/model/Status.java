package com.internship.amazingtaxiservice.taxiservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Entity
@Table(name = "status")
@Data
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "taxi_status")
    @NotEmpty(message = "Taxi should not be blank")
    private String taxiStatus;

    public Status() {
    }

    public Status(String busy) {
        this.taxiStatus = busy;
    }

    public Status(int id, String status) {
        this.id = id;
        this.taxiStatus = status;
    }

}