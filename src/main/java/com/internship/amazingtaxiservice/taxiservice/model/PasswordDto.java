package com.internship.amazingtaxiservice.taxiservice.model;

import lombok.*;

@Data
public class PasswordDto {

    private String newPassword;
    private String resetToken;
}
