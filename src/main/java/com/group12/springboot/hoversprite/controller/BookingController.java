package com.group12.springboot.hoversprite.controller;

import com.group12.springboot.hoversprite.dataTransferObject.request.booking.BookingCancelRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.booking.BookingConfirmationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.booking.BookingCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.BookingResponse;
import com.group12.springboot.hoversprite.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/create/pending")
    ApiResponse<BookingResponse> createPendingBooking(@RequestBody @Valid BookingCreationRequest request){
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.createPendingBooking(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }

    @PostMapping("/create/confirmed")
    ApiResponse<BookingResponse> createConfirmedBooking(@RequestBody @Valid BookingCreationRequest request){
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.createConfirmedBooking(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }

    @PostMapping("/statusChange/cancel")
    ApiResponse<BookingResponse> cancelBooking(@RequestBody BookingCancelRequest request){
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.cancelBooking(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }

    @PostMapping("/statusChange/confirmed")
    ApiResponse<BookingResponse> confirmBooking(@RequestBody BookingConfirmationRequest request){
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.confirmBooking(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }
}
