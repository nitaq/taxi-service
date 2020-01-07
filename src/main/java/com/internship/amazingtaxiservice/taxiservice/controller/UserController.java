package com.internship.amazingtaxiservice.taxiservice.controller;

import com.internship.amazingtaxiservice.taxiservice.model.User;
import com.internship.amazingtaxiservice.taxiservice.service.MailService;
import com.internship.amazingtaxiservice.taxiservice.service.UserService;
import com.internship.amazingtaxiservice.taxiservice.utils.BadRequestAlertException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/api/")
public class UserController {

    private UserService userService;

    private MailService mailService;


    @Autowired
    public UserController(UserService userService,MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }


    @ApiOperation(value = "Get user by id")
    @GetMapping("user/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        User user = userService.findById(id);
        return ResponseEntity.ok().body(user);
    }


    @ApiOperation(value = "Get all users")
    @GetMapping("/users")
    public ResponseEntity<Page<User>> findAllUsers(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok().body(users);
    }


    @ApiOperation(value = "Get user by token")
    @GetMapping("/userByToken")
    public ResponseEntity<User> getUserByToken(@RequestHeader(name = "Authorization") String token) {
        User user = userService.findUserByToken(token);
        return ResponseEntity.ok().body(user);
    }


    @ApiOperation(value = "Create User")
    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getId() == 0) {
            throw new BadRequestAlertException("A new Task cannot already have an ID");
        }
        userService.save(user);
        mailService.sendVerificationMail("http://localhost:8080", user);

        return ResponseEntity.ok().body(user);
    }


    @ApiOperation(value = "Delete User")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        userService.deleteById(id);
        Void user = null;
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "Update User")
    @PutMapping("/user")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.ok().body(user);
    }


    @ApiOperation(value = "Search users")
    @GetMapping("/searchUser")
    public ResponseEntity<List<User>> query(@RequestParam(value = "search") String query) {
        List<User> result = null;
        try {
            result = userService.searchByQuery(query);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}