package com.group12.springboot.hoversprite.timeslot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.springboot.hoversprite.common.ApiResponse;
import com.group12.springboot.hoversprite.timeslot.TimeSlotByDateRequest;
import com.group12.springboot.hoversprite.timeslot.TimeSlotByDateResponse;
import com.group12.springboot.hoversprite.timeslot.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.timeslot.TimeSlotCreateResponse;
import com.group12.springboot.hoversprite.timeslot.service.TimeSlotService;

import jakarta.validation.Valid;

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