package com.group12.springboot.hoversprite.controller;

import com.group12.springboot.hoversprite.dataTransferObject.request.feedback.FeedbackCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.FeedbackCreationResponse;
import com.group12.springboot.hoversprite.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    ApiResponse<FeedbackCreationResponse> createFeedback(@RequestBody FeedbackCreationRequest request){
        ApiResponse<FeedbackCreationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(feedbackService.createFeedBack(request));
        return apiResponse;
    }
}
