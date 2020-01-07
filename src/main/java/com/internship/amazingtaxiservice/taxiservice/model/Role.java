package com.internship.amazingtaxiservice.taxiservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @Column(name = "title")
    @NotEmpty(message = "Title should not be blank")
    private String title;

    public Role() {
    }

    public Role(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Role(String admin) {
        this.title = admin;
    }

}