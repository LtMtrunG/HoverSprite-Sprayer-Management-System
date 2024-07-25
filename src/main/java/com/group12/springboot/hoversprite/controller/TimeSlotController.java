package com.group12.springboot.hoversprite.controller;

import com.group12.springboot.hoversprite.dataTransferObject.request.TimeSlotByDateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.TimeSlotByDateResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.TimeSlotCreateResponse;
import com.group12.springboot.hoversprite.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/timeslots")
public class TimeSlotController {
    @Autowired
    private TimeSlotService timeSlotService;

    @PostMapping
    ApiResponse<TimeSlotCreateResponse> createTimeSlot(@RequestBody TimeSlotCreateRequest request){
        ApiResponse<TimeSlotCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(timeSlotService.createTimeSlot(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<TimeSlotByDateResponse> getTimeSlotByDate(@RequestBody TimeSlotByDateRequest request){
        ApiResponse<TimeSlotByDateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(timeSlotService.getTimeSlotByDate(request));
        return apiResponse;
    }
}
