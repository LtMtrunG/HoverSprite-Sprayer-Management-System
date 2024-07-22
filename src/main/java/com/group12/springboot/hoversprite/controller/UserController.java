package com.group12.springboot.hoversprite.controller;

import com.group12.springboot.hoversprite.dataTransferObject.request.UserCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.UserUpdateRequest;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    User createUser(@RequestBody @Valid UserCreationRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    User getUserById(@PathVariable("userId") String userId){
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}")
    User updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    boolean deleteUser(@PathVariable("userId") String userId){
        userService.deleteUser(userId);
        return true;
    }
}
