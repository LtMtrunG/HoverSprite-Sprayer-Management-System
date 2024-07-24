//package com.group12.springboot.hoversprite.service;
//
//import com.group12.springboot.hoversprite.dataTransferObject.request.BookingCreationRequest;
//import com.group12.springboot.hoversprite.dataTransferObject.response.BookingResponse;
//import com.group12.springboot.hoversprite.entity.Booking;
//import com.group12.springboot.hoversprite.entity.User;
//import com.group12.springboot.hoversprite.exception.CustomException;
//import com.group12.springboot.hoversprite.exception.ErrorCode;
//import com.group12.springboot.hoversprite.repository.BookingRepository;
//import com.group12.springboot.hoversprite.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class BookingService {
//    @Autowired
//    BookingRepository bookingRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @PreAuthorize("hasRole('RECEPTIONIST')")
//    public BookingResponse createBooking(BookingCreationRequest request){
//        Booking booking = new Booking();
//
//        booking.setReceptionist(request.getReceptionist());
//        booking.setUser(request.getUser());
//        booking.setSprayers(request.getSprayers());
//        booking.setCropType(request.getCropType());
//        booking.setStatus(request.getStatus());
//        booking.setFarmlandArea(request.getFarmlandArea());
//        booking.setCreatedTime(request.getCreatedTime());
//        booking.setSprayingTime(request.getSprayingTime());
//        booking.setTotalCost(request.getTotalCost());
//
//        return new BookingResponse(booking);
//    }
//
//    @PreAuthorize("hasRole('RECEPTIONIST')")
//    public List<BookingResponse> getBookings(){
//        List<Booking> bookings = bookingRepository.findAll();
//        return  bookings.stream()
//                .map(BookingResponse::new)
//                .collect(Collectors.toList());
//    }
//
//    public BookingResponse getBookingById(Long bookingId){
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new RuntimeException("Booking Not Found."));
//        return new BookingResponse(booking);
//    }
//
//    @PreAuthorize("hasRole('FARMER')")
//    public List<BookingResponse> getMyBookings(){
//        var context = SecurityContextHolder.getContext();
//        String email = context.getAuthentication().getName();
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));
//        List<Booking> bookings = bookingRepository.findByUser(user);
//        return bookings.stream()
//                .map(BookingResponse::new)
//                .collect(Collectors.toList());
//    }
//
//
//}
