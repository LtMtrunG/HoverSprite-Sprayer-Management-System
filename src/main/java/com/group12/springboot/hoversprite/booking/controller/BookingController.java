package com.group12.springboot.hoversprite.booking.controller;

import com.group12.springboot.hoversprite.booking.*;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group12.springboot.hoversprite.booking.service.BookingService;
import com.group12.springboot.hoversprite.common.ApiResponse;
import com.group12.springboot.hoversprite.common.ListResponse;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

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
     ApiResponse<BookingResponse> inProgressBooking(@RequestParam("id") Long id) {
         ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
         BookingResponse bookingResponse = bookingService.inProgressBooking(id);
         apiResponse.setResult(bookingResponse);
         return apiResponse;
     }

    @PostMapping("/assign")
    ApiResponse<BookingResponse> assignSprayers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestBody @Valid BookingAssignRequest request) {
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.assignSprayers(request));
        return apiResponse;
    }

    @PostMapping("/completeByFarmer")
    ApiResponse<BookingResponse> completeBookingByFarmer(@RequestBody BookingCompleteRequest request) {
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        BookingResponse bookingResponse = bookingService.completeBookingByFarmer(request);
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

    @GetMapping()
    ApiResponse<Page<BookingResponse>> getBookings(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                                   @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
                                                   @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword){
        ApiResponse<Page<BookingResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookings(pageNo, pageSize, status, keyword));
        return apiResponse;
    }

    @GetMapping("/myBookings")
    ApiResponse<Page<BookingResponse>> getMyBookings(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                                             @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
                                                     @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword){
        ApiResponse<Page<BookingResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getMyBookings(pageNo, pageSize, status, keyword));
        return apiResponse;
    }

    @GetMapping("/assignedBookings")
    ApiResponse<Page<BookingResponse>> getAssignedBookings(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                     @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                                     @RequestParam(value = "status", defaultValue = "ALL", required = false) String status,
                                                     @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword){
        ApiResponse<Page<BookingResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getAssignedBookings(pageNo, pageSize, status, keyword));
        return apiResponse;
    }

    @GetMapping("/sprayers/available")
    ApiResponse<Page<AvailableSprayersResponse>> getAvailableSprayers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam("id") @Valid Long timeSlotId) {
        ApiResponse<Page<AvailableSprayersResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getAvailableSprayersByTimeSlot(pageNo, pageSize, timeSlotId));
        return apiResponse;
    }

     @GetMapping("/{bookingId}")
     ApiResponse<BookingResponse> getBookingById(@PathVariable("bookingId") Long bookingId) throws AccessDeniedException {
         ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
         apiResponse.setResult(bookingService.getBookingById(bookingId));
         return apiResponse;
     }

    @GetMapping("/myBookings/byWeek")
    ApiResponse<List<BookingResponse>> getMyBookingsByWeek(@RequestParam("date")  String date){
        ApiResponse<List<BookingResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getMyBookingsByWeek(date));
        return apiResponse;
    }


    // @PutMapping("/{bookingId}")
    // ApiResponse<BookingResponse> updateBooking(@RequestBody BookingUpdateRequest request) throws AccessDeniedException {
    //     ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
    //     apiResponse.setResult(bookingService.updateBooking(request));
    //     return apiResponse;
    // }

    @DeleteMapping("/{bookingId}")
    ApiResponse<String> deleteBooking(@PathVariable("bookingId") Long bookingId){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        bookingService.deleteBooking(bookingId);
        apiResponse.setResult("Booking is deleted");
        return apiResponse;
    }

    @GetMapping("/sprayer/route")
    ApiResponse<List<double[]>> getBookingRoute(@RequestParam("date") String date) {
        ApiResponse<List<double[]>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookingRoute(date));
        return apiResponse;
    }
}