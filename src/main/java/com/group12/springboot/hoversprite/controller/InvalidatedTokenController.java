package com.group12.springboot.hoversprite.controller;

import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import com.group12.springboot.hoversprite.service.InvalidatedTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

public class InvalidatedTokenController {
    @Autowired
    InvalidatedTokenService invalidatedTokenService;

    @GetMapping("/cleanExpiredTokens")
    public ApiResponse<Void> cleanExpiredTokens() {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        invalidatedTokenService.cleanExpiredTokens();
        return apiResponse;
    }
}
