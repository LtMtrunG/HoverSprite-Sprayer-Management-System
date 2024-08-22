package com.group12.springboot.hoversprite.feedback.controller;

import com.group12.springboot.hoversprite.common.ApiResponse;
import com.group12.springboot.hoversprite.feedback.FeedbackCreationRequest;
import com.group12.springboot.hoversprite.feedback.FeedbackCreationResponse;
import com.group12.springboot.hoversprite.feedback.FeedbackResponse;
import com.group12.springboot.hoversprite.feedback.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/booking/feedback/create")
    ApiResponse<FeedbackCreationResponse> createFeedback(@RequestBody FeedbackCreationRequest request){
        ApiResponse<FeedbackCreationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(feedbackService.createFeedBack(request));
        return apiResponse;
    }

    @GetMapping("/booking/feedback/{feedbackId}")
    ApiResponse<FeedbackResponse> getFeedbackById(@PathVariable("feedbackId") Long feedbackId) {
        ApiResponse<FeedbackResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(feedbackService.getFeedbackById(feedbackId));
        return apiResponse;
    }

}
