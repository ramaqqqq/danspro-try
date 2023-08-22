package com.micro.demo.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.micro.demo.handlers.UserHandlers;
import com.micro.demo.helpers.FormatErr;
import com.micro.demo.helpers.Helpers;
import com.micro.demo.models.entity.UserEntity;

@RestController
public class UsersControllers {

    private final UserHandlers userHandlers;

    @Autowired
    public UsersControllers(UserHandlers userHandlers) {
        this.userHandlers = userHandlers;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserEntity userEntity) {
        try {
            Map<String, Object> response = userHandlers.addUser(userEntity);
            Helpers.log("info", "Successfully to Register");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Exception formattedError = FormatErr.formatError(e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            Helpers.log("error", "In Server: "+formattedError.getMessage());
            errorResponse.put("error", "Email sudah digunakan, msg : "+formattedError.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserEntity userEntity) {
        try {
            Map<String, Object> response = userHandlers.loginUser(userEntity);
            Helpers.log("info", "Success to Login");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            Helpers.log("error", "In Server: " + e.getMessage());
            errorResponse.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
