package com.group12.springboot.hoversprite.controller;

import com.group12.springboot.hoversprite.dataTransferObject.request.booking.*;
import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.BookingResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.ListResponse;
import com.group12.springboot.hoversprite.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

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

    @PostMapping("/cancel")
    ApiResponse<BookingResponse> cancelBooking(@RequestBody BookingCancelRequest request){
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.cancelBooking(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }

    @PostMapping("/confirmed")
    ApiResponse<BookingResponse> confirmBooking(@RequestBody BookingConfirmationRequest request){
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.confirmBooking(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }

    @PostMapping("/inProgress")
    ApiResponse<BookingResponse> inProgressBooking(@RequestBody BookingInProgressRequest request) throws AccessDeniedException {
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.inProgressBooking(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }

    @PostMapping("/completeBySprayer")
    ApiResponse<BookingResponse> completeBookingBySprayer(@RequestBody BookingCompleteRequest request) throws AccessDeniedException {
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.completeBookingBySprayer(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }

    @PostMapping("/completeByFarmer")
    ApiResponse<BookingResponse> completeBookingByFarmer(@RequestBody BookingCompleteRequest request) {
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.completeBookingByFarmer(request);
        apiResponse.setResult(bookingResponse);
        return apiResponse;
    }

    @GetMapping()
    ApiResponse<ListResponse<BookingResponse>> getBookings(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize){
        ApiResponse<ListResponse<BookingResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookings(pageNo, pageSize));
        return apiResponse;
    }

    @GetMapping("/myBookings")
    ApiResponse<ListResponse<BookingResponse>> getMyBookings(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize){
        ApiResponse<ListResponse<BookingResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getMyBookings(pageNo, pageSize));
        return apiResponse;
    }

    @GetMapping("/{bookingId}")
    ApiResponse<BookingResponse> getBookingById(@PathVariable("bookingId") Long bookingId) throws AccessDeniedException {
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookingById(bookingId));
        return apiResponse;
    }

    @PutMapping("/{bookingId}")
    ApiResponse<BookingResponse> updateBooking(@RequestBody BookingUpdateRequest request) throws AccessDeniedException {
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.updateBooking(request));
        return apiResponse;
    }

    @DeleteMapping("/{bookingId}")
    ApiResponse<String> deleteBooking(@PathVariable("bookingId") Long bookingId){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        bookingService.deleteBooking(bookingId);
        apiResponse.setResult("Booking is deleted");
        return apiResponse;
    }
}
