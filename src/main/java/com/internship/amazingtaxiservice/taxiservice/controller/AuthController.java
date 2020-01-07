package com.internship.amazingtaxiservice.taxiservice.controller;

import com.internship.amazingtaxiservice.taxiservice.model.*;
import com.internship.amazingtaxiservice.taxiservice.config.JwtTokenProvider;
import com.internship.amazingtaxiservice.taxiservice.service.MailService;
import com.internship.amazingtaxiservice.taxiservice.service.UserService;
import com.internship.amazingtaxiservice.taxiservice.utils.AccountNotActivatedException;
import com.internship.amazingtaxiservice.taxiservice.utils.InvalidPasswordOrUsernameException;
import com.internship.amazingtaxiservice.taxiservice.utils.UserDoesNotExistException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/api/")
public class AuthController {

    private AuthenticationManager authenticationManager;

     private JwtTokenProvider jwtTokenProvider;


    private  MailService mailService;

    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService,PasswordEncoder passwordEncoder,MailService mailService){
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }


    @ApiOperation(value = "User Signin")
    @PostMapping("/auth/signin")
    public ResponseEntity<LoginResponseDTO> signin(@RequestBody LoginDto loginDto) throws Exception {

        User user = userService.findByUsername(loginDto.getUsername());
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();

        if (user != null) {
            if (checkIfValidOldPassword(user.getPassword(), loginDto.getPassword())) {
                if (user.isEnabled()) {
                    String token = jwtTokenProvider.createToken(loginDto.getUsername(), Collections.singletonList(this.userService.findByUsername(loginDto.getUsername()).getRole().getTitle()));

                    authenticate(user.getUsername(), loginDto.getPassword());

                    loginResponseDTO.setToken(token);
                    loginResponseDTO.setUsername(loginDto.getUsername());
                } else {
                    throw new AccountNotActivatedException(loginDto.getUsername());
                }
            } else {
                throw new InvalidPasswordOrUsernameException();
            }

        } else {
            throw new UserDoesNotExistException();
        }

        return ResponseEntity.status(200).body(loginResponseDTO);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new Exception();
        }
    }


    @ApiOperation(value = "Register User")
    @PostMapping("/auth/register")
    public ResponseEntity<Object> register(@RequestBody UserDto userDto) {
        User user = userService.saveUserDto(userDto);
        mailService.sendVerificationMail("http://localhost:8080", user);
        return ResponseEntity.status(204).build();
    }


    @ApiOperation(value = "Activate User")
    @PostMapping("/auth/activateUser")
    public ResponseEntity<Object> activateUser(@RequestParam String token) {
        userService.activateUser(token);
        return ResponseEntity.status(204).build();
    }


    @ApiOperation(value = "Resend Activation Email")
    @PostMapping("/auth/resendActivationEmail")
    public ResponseEntity<Object> resendActivationEmail(@RequestParam String username) {
        userService.resendActivationEmail(username);
        return ResponseEntity.status(204).build();
    }


    @ApiOperation(value = "Forgot Password")
    @PostMapping("/auth/user/forgotPassword")
    @ResponseBody
    public ResponseEntity<Object> resetPassword(@RequestParam("username") String username) {
        userService.createPasswordResetTokenForUser(username);
        return ResponseEntity.status(204).build();
    }


    @ApiOperation(value = "Change Password")
    @PostMapping("user/changePassword")
    @ResponseBody
    public ResponseEntity<Object> changePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        userService.updatePassword(updatePasswordDto);
        return ResponseEntity.status(204).build();
    }


    @ApiOperation(value = "Save Password")
    @PostMapping("user/savePassword")
    @ResponseBody
    public ResponseEntity<Object> savePassword(@RequestBody PasswordDto passwordDto) {
        userService.changeUserPassword(passwordDto);
        return ResponseEntity.status(204).build();
    }


    public boolean checkIfValidOldPassword(String databasePassword, String oldPassword) {
        return passwordEncoder.matches(oldPassword, databasePassword);
    }


    @ApiOperation(value = "Change Email Request")
    @PostMapping("auth/user/requestChangeEmail")
    @ResponseBody
    public ResponseEntity<Object> requestchangeEmail(@RequestParam int userId, @RequestParam String email) {
        userService.requestChangeUserEmail(userId, email);
        return ResponseEntity.status(204).build();
    }


    @ApiOperation(value = "Change Email")
    @PostMapping("auth/user/changeEmail")
    @ResponseBody
    public ResponseEntity<Object> changeEmail(@RequestParam String token, @RequestParam String email) {
        userService.changeEmail(token, email);
        return ResponseEntity.status(204).build();
    }


    @ApiOperation(value = "Get user profile")
    @GetMapping("auth/user/userProfile")
    @ResponseBody
    public ResponseEntity<Object> getUserProfile(@RequestParam String username) {
        return ResponseEntity.status(200).body(userService.getUserProfile(username));
    }


    @ApiOperation(value = "Change User Role")
    @PostMapping("auth/user/changeUserRole")
    @ResponseBody
    public ResponseEntity changeUserRole(@RequestParam int userId, @RequestParam int roleId) {
        userService.changeUserRole(userId, roleId);
        return ResponseEntity.status(204).build();
    }

}