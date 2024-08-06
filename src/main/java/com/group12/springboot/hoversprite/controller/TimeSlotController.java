package com.group12.springboot.hoversprite.controller;

import com.group12.springboot.hoversprite.dataTransferObject.request.timeslot.TimeSlotByDateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.timeslot.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.TimeSlotByDateResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.TimeSlotCreateResponse;
import com.group12.springboot.hoversprite.service.TimeSlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timeslots")
public class TimeSlotController {
    @Autowired
    private TimeSlotService timeSlotService;

    @PostMapping
    ApiResponse<TimeSlotCreateResponse> createTimeSlot(@RequestBody @Valid TimeSlotCreateRequest request){
        ApiResponse<TimeSlotCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(timeSlotService.createTimeSlot(request));
        return apiResponse;
    }

    @GetMapping("/byDate")
    ApiResponse<TimeSlotByDateResponse> getTimeSlotByDate(@RequestBody TimeSlotByDateRequest request){
        ApiResponse<TimeSlotByDateResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(timeSlotService.getTimeSlotByDate(request));
        return apiResponse;
    }

    @GetMapping("/byWeek")
    ApiResponse<List<TimeSlotByDateResponse>> getTimeSlotByWeek(@RequestBody TimeSlotByDateRequest request){
        ApiResponse<List<TimeSlotByDateResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(timeSlotService.getTimeSlotByWeek(request));
        return apiResponse;
    }
}