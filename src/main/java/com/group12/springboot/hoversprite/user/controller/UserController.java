package com.group12.springboot.hoversprite.user.controller;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.springboot.hoversprite.common.ApiResponse;
import com.group12.springboot.hoversprite.common.ListResponse;
import com.group12.springboot.hoversprite.user.FarmerCreationRequest;
import com.group12.springboot.hoversprite.user.FarmerExternalSignUpInfoRequest;
import com.group12.springboot.hoversprite.user.FarmerExternalSignUpInfoResponse;
import com.group12.springboot.hoversprite.user.ReceptionistCreationRequest;
import com.group12.springboot.hoversprite.user.SprayerCreationRequest;
import com.group12.springboot.hoversprite.user.UserResponse;
import com.group12.springboot.hoversprite.user.UserUpdateRequest;
import com.group12.springboot.hoversprite.user.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/register/farmer/external")
    ApiResponse<FarmerExternalSignUpInfoResponse> receiveFarmerGmailInfo(@RequestBody @Valid FarmerExternalSignUpInfoRequest request) {
        ApiResponse<FarmerExternalSignUpInfoResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.receiveFarmerGmailInfo(request));
        return apiResponse;
    }

    @PostMapping("/register/farmer")
    ApiResponse<UserResponse> createFarmer(@RequestBody @Valid FarmerCreationRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createFarmer(request));

        return apiResponse;
    }

    @PostMapping("register/receptionist")
    ApiResponse<UserResponse> createReceptionist(@RequestBody @Valid ReceptionistCreationRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createReceptionist(request));
        return apiResponse;
    }

    @PostMapping("register/sprayer")
    ApiResponse<UserResponse> createSprayer(@RequestBody @Valid SprayerCreationRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createSprayer(request));
        return apiResponse;
    }

    @GetMapping("/farmers")
    ApiResponse<ListResponse<UserResponse>> getFarmers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        ApiResponse<ListResponse<UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUsersByRole(pageNo, pageSize, "FARMER"));
        return apiResponse;
    }

    @GetMapping("/sprayers")
    ApiResponse<ListResponse<UserResponse>> getSprayers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        ApiResponse<ListResponse<UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUsersByRole(pageNo, pageSize, "SPRAYER"));
        return apiResponse;
    }

    @GetMapping("/user/id/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserById(userId));
        return apiResponse;
    }

    @GetMapping("/user/phone/{phoneNumber}")
    ApiResponse<UserResponse> getUserByPhone(@PathVariable("phoneNumber") String phoneNumber) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserByPhone(phoneNumber));
        return apiResponse;
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getMyInfo());
        return apiResponse;
    }

    @PutMapping("/user/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable("userId") Long userId, @RequestBody UserUpdateRequest request)
            throws AccessDeniedException {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(userId, request));
        return apiResponse;
    }

    @DeleteMapping("/user/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") Long userId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        userService.deleteUser(userId);
        apiResponse.setResult("User is deleted");
        return apiResponse;
    }
}