package com.group12.springboot.hoversprite.controller;

import com.group12.springboot.hoversprite.dataTransferObject.request.UserCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.UserUpdateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.ListResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.UserResponse;
import com.group12.springboot.hoversprite.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));

        return apiResponse;
    }

    @GetMapping
    ApiResponse<ListResponse<UserResponse>> getUsers(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                     @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize){
        ApiResponse<ListResponse<UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUsers(pageNo, pageSize));
        return apiResponse;
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") Long userId){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserById(userId));
        return apiResponse;
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getMyInfo());
        return apiResponse;
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable("userId") Long userId, @RequestBody UserUpdateRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(userId, request));
        return apiResponse;
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") Long userId){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        userService.deleteUser(userId);
        apiResponse.setResult("User is deleted");
        return apiResponse;
    }
}
