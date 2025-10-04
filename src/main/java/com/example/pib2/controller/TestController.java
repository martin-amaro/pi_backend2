package com.example.pib2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.entity.User;
import com.example.pib2.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Map<String, String> test() {

        return Map.of("test", "example");
        
    }

    @GetMapping("users")
    public List<User> getAllUsers() {
        
        return userService.getAll();

    }
    
    
}
