package com.internship.amazingtaxiservice.taxiservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "taxi")
@Data
public class Taxi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotEmpty(message = "Name should not be blank")
    private String name;

    @Column(name = "number")
    private int number;


    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @NotNull
    private Status status;

}