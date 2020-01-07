package com.internship.amazingtaxiservice.taxiservice.utils;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class Validation {

    public static boolean isPasswordValid(String password) {
        final Pattern PASSWORD_REGEX = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{8,20}$", Pattern.CASE_INSENSITIVE);
        return PASSWORD_REGEX.matcher(password).matches();
    }

    public static boolean passwordMatch(String newPass, String confirmNewPass) {
        return newPass.equals(confirmNewPass);

    }

}
