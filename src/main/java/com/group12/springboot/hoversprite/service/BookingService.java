package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.request.booking.BookingCancelRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.booking.BookingConfirmationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.booking.BookingCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.timeslot.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.BookingResponse;
import com.group12.springboot.hoversprite.entity.Booking;
import com.group12.springboot.hoversprite.entity.TimeSlot;
import com.group12.springboot.hoversprite.entity.enums.BookingStatus;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.repository.BookingRepository;
import com.group12.springboot.hoversprite.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private BookingRepository bookingRepository;

    @PreAuthorize("hasRole('FARMER')")
    public BookingResponse createPendingBooking(BookingCreationRequest request) {
        LocalDate date = request.getDate();
        LocalTime startTime = request.getStartTime();

        TimeSlot timeSlot = checkOrCreateTimeSlot(date, startTime);

        if (timeSlot.isAvailable()) {
            timeSlot.bookSession();
            timeSlotRepository.save(timeSlot);
        } else {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        Booking booking = createBookingWithStatus(BookingStatus.PENDING, request, timeSlot);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse createConfirmedBooking(BookingCreationRequest request) {
        LocalDate date = request.getDate();
        LocalTime startTime = request.getStartTime();

        TimeSlot timeSlot = checkOrCreateTimeSlot(date, startTime);

        if (timeSlot.isAvailable()) {
            timeSlot.bookSession();
            timeSlotRepository.save(timeSlot);
        } else {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        Booking booking = createBookingWithStatus(BookingStatus.CONFIRMED, request, timeSlot);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse cancelBooking(BookingCancelRequest request) {
        Optional<Booking> optionalBooking = bookingRepository.findById(request.getId());

        if(optionalBooking.isEmpty()){
            throw new CustomException(ErrorCode.BOOKING_NOT_EXISTS);
        }

        Booking booking = new Booking(optionalBooking.get());
        booking.setStatus(BookingStatus.CANCELLED);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse confirmBooking(BookingConfirmationRequest request) {
        Optional<Booking> optionalBooking = bookingRepository.findById(request.getId());

        if(optionalBooking.isEmpty()){
            throw new CustomException(ErrorCode.BOOKING_NOT_EXISTS);
        }

        Booking booking = new Booking(optionalBooking.get());
        booking.setStatus(BookingStatus.CONFIRMED);

        return new BookingResponse(booking);
    }

    private TimeSlot checkOrCreateTimeSlot(LocalDate date, LocalTime startTime) {
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findByDateAndStartTime(date, startTime);

        if (optionalTimeSlot.isPresent()) {
            return optionalTimeSlot.get();
        } else {
            TimeSlotCreateRequest createRequest = new TimeSlotCreateRequest();
            createRequest.setDate(date);
            createRequest.setStartTime(startTime);

            timeSlotService.createTimeSlot(createRequest);

            return timeSlotRepository.findByDateAndStartTime(date, startTime)
                    .orElseThrow(() -> new RuntimeException("Failed to create time slot"));
        }
    }

    private Booking createBookingWithStatus(BookingStatus status, BookingCreationRequest request, TimeSlot timeslot){
        Booking booking = new Booking();
        booking.setUser(request.getUser());
        booking.setCropType(request.getCropType());
        booking.setStatus(BookingStatus.PENDING);
        booking.setFarmlandArea(request.getFarmlandArea());
        booking.setCreatedTime(request.getCreatedTime());
        booking.setTimeSlot(timeslot);
        booking.setTotalCost(booking.getFarmlandArea() * 30000);

        if(status == BookingStatus.CONFIRMED & request.getReceptionist() != null){
            booking.setReceptionist(request.getReceptionist());
        }

        booking = bookingRepository.save(booking);

        return booking;
    }



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


}
