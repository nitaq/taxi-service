package com.internship.amazingtaxiservice.taxiservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "booking")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;

    @Column(name = "pickup_location")
    @NotEmpty(message = "Pickup location should not be blank")
    private String pickupLocation;

    @Column(name = "destination")
    @NotEmpty(message = "Destination should not be blank")
    private String destination;

    @Column(name = "is_reserved")
    private boolean isReserved;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    @NotNull
    private User users;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "taxi_id")
    private Taxi taxi;
}