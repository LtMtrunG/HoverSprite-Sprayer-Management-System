package com.group12.springboot.hoversprite.authentication.controller;

import com.group12.springboot.hoversprite.authentication.AuthenticationRequest;
import com.group12.springboot.hoversprite.authentication.AuthenticationResponse;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenRequest;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenResponse;
import com.group12.springboot.hoversprite.authentication.service.AuthenticationService;
import com.group12.springboot.hoversprite.common.ApiResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request){
        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
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
}