package com.group12.springboot.hoversprite.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.springboot.hoversprite.dataTransferObject.request.auth.AuthenticationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.auth.IntrospectTokenRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.AuthenticationResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.IntrospectTokenResponse;
import com.group12.springboot.hoversprite.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        apiResponse.setResult(authenticationResponse);
        return apiResponse;
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectTokenResponse> introspect(@RequestBody IntrospectTokenRequest request) throws ParseException, JOSEException {
        ApiResponse<IntrospectTokenResponse> apiResponse = new ApiResponse<>();
        IntrospectTokenResponse introspectTokenResponse = authenticationService.introspect(request);
        apiResponse.setResult(introspectTokenResponse);
        return apiResponse;
    }
}