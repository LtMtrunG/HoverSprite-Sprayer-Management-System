package com.group12.springboot.hoversprite.authentication.controller;

import com.group12.springboot.hoversprite.authentication.AuthenticationRequest;
import com.group12.springboot.hoversprite.authentication.AuthenticationResponse;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenRequest;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenResponse;
import com.group12.springboot.hoversprite.authentication.service.AuthenticationService;
import com.group12.springboot.hoversprite.common.ApiResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.text.ParseException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    private static final String CLIENT_ID = "407408718192.apps.googleusercontent.com";
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request, HttpServletResponse response){
        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request, response);
        apiResponse.setResult(authenticationResponse);
        return apiResponse;
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectTokenResponse> introspect(@RequestBody @Valid IntrospectTokenRequest request) throws ParseException, JOSEException {
        ApiResponse<IntrospectTokenResponse> apiResponse = new ApiResponse<>();
        IntrospectTokenResponse introspectTokenResponse = authenticationService.introspect(request);
        apiResponse.setResult(introspectTokenResponse);
        return apiResponse;
    }

    @GetMapping("/logout")
    ApiResponse<String> logout(HttpServletRequest request, HttpServletResponse response){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        authenticationService.logout(request, response);
        apiResponse.setResult("Logout successfully");
        return apiResponse;
    }
}