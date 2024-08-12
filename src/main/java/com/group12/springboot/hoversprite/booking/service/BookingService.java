package com.group12.springboot.hoversprite.booking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.springboot.hoversprite.booking.BookingCancelRequest;
import com.group12.springboot.hoversprite.booking.BookingCompleteRequest;
import com.group12.springboot.hoversprite.booking.BookingConfirmationRequest;
import com.group12.springboot.hoversprite.booking.BookingCreationRequest;
import com.group12.springboot.hoversprite.booking.BookingResponse;
import com.group12.springboot.hoversprite.booking.entity.Booking;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.booking.repository.BookingRepository;
import com.group12.springboot.hoversprite.common.ListResponse;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.timeslot.TimeSlotAPI;
import com.group12.springboot.hoversprite.timeslot.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.timeslot.TimeSlotDTO;
import com.group12.springboot.hoversprite.user.FarmerDTO;
import com.group12.springboot.hoversprite.user.ReceptionistDTO;
import com.group12.springboot.hoversprite.user.UserAPI;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EmailService emailService;
    private final TimeSlotAPI timeSlotAPI;
    private final UserAPI userAPI;

    @PreAuthorize("hasRole('FARMER')")
    public BookingResponse createPendingBooking(BookingCreationRequest request) {
        TimeSlotDTO timeSlotDTO = checkOrCreateTimeSlot(request.getDate(), request.getStartTime());

        FarmerDTO farmerDTO = userAPI.findFarmerById(request.getFarmerId());

        if (!timeSlotAPI.isAvailable(timeSlotDTO)) {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        timeSlotAPI.bookSession(timeSlotDTO);

        Booking booking = createBookingWithStatus(BookingStatus.PENDING, request, timeSlotDTO.getId(), farmerDTO.getId(), null,Collections.EMPTY_LIST);
        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse createConfirmedBooking(BookingCreationRequest request) {
        TimeSlotDTO timeSlotDTO = checkOrCreateTimeSlot(request.getDate(), request.getStartTime());

        FarmerDTO farmerDTO = userAPI.findFarmerById(request.getFarmerId());
        ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(request.getReceptionistId());
        // Optional<User> farmerOptional = userService.findByEmail(request.getFarmer());
        // Optional<User> receptionistOptional = userService.findByEmail(request.getReceptionist());

        // User farmer = farmerOptional.orElseThrow(() -> new CustomException(ErrorCode.FARMER_NOT_EXIST));
        // User receptionist = receptionistOptional
        //         .orElseThrow(() -> new CustomException(ErrorCode.RECEPTIONIST_NOT_EXIST));

        if (!timeSlotAPI.isAvailable(timeSlotDTO)) {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        timeSlotAPI.bookSession(timeSlotDTO);

        Booking booking = createBookingWithStatus(BookingStatus.CONFIRMED, request, timeSlotDTO.getId(), farmerDTO.getId(), receptionistDTO.getId(),
                Collections.EMPTY_LIST);

        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse cancelBooking(BookingCancelRequest request) {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse confirmBooking(BookingConfirmationRequest request) {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(request.getReceptionistId());

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setReceptionistId(receptionistDTO.getId());
        bookingRepository.save(booking);

        emailService.sendEmail(booking.getFarmerId(), booking);

        return new BookingResponse(booking);
    }

    // public BookingResponse inProgressBooking(BookingInProgressRequest request) throws AccessDeniedException {
    //     Booking booking = bookingRepository.findById(request.getId())
    //             .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

    //     if (booking.getStatus() != BookingStatus.ASSIGNED) {
    //         throw new CustomException(ErrorCode.INVALID_ACTION);
    //     }

    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String currentUsername = authentication.getName();
    //     checkAuthorization(booking, authentication, currentUsername, "ROLE_SPRAYER");

    //     User currentUser = userService.findByEmail(currentUsername)
    //             .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

    //     booking.setStatus(BookingStatus.IN_PROGRESS);
    //     bookingRepository.save(booking);

    //     return new BookingResponse(booking);
    // }

    // public BookingResponse completeBookingBySprayer(BookingCompleteRequest request) throws AccessDeniedException {
    //     Booking booking = bookingRepository.findById(request.getId())
    //             .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

    //     if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
    //         throw new CustomException(ErrorCode.INVALID_ACTION);
    //     }

    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //      currentUsername = authentication.getName();
    //     checkAuthorization(booking, authentication, currentUsername, "ROLE_SPRAYER");

    //     User currentUser = userService.findByEmail(currentUsername)
    //             .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

    //     booking.setStatus(BookingStatus.COMPLETED_BY_SPRAYER);
    //     bookingRepository.save(booking);

    //     return new BookingResponse(booking);
    // }

    @PostAuthorize("booking.farmerId == T(java.lang.Long).parseLong(authentication.subject)")
    public BookingResponse completeBookingByFarmer(BookingCompleteRequest request) {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Booking Not Found."));

        if (booking.getStatus() != BookingStatus.COMPLETED_BY_SPRAYER) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        // User farmer = getCurrentUser();

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ListResponse<BookingResponse> getBookings(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Booking> bookingPage = bookingRepository.findAllOrderByStatus(pageable);

        List<BookingResponse> bookingResponses = bookingPage.getContent().stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());

        return createListResponse(bookingPage, bookingResponses);
    }

    @PreAuthorize("hasRole('FARMER')")
    public ListResponse<BookingResponse> getMyBookings(int pageNo, int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
        Long farmerId = Long.parseLong(jwt.getSubject());

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Booking> bookingPage = bookingRepository.findByFarmerIdOrderByStatus(farmerId, pageable);

        List<BookingResponse> bookingResponses = bookingPage.getContent().stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());

        return createListResponse(bookingPage, bookingResponses);
    }

    // public BookingResponse getBookingById(Long bookingId) throws AccessDeniedException {
    //     Booking booking = bookingRepository.findById(bookingId)
    //             .orElseThrow(() -> new RuntimeException("Booking Not Found."));

    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String currentUsername = authentication.getName();
    //     checkAuthorization(booking, authentication, currentUsername, "ROLE_RECEPTIONIST");

    //     return new BookingResponse(booking);
    // }

    // public BookingResponse updateBooking(BookingUpdateRequest request) throws AccessDeniedException {
    //     Booking booking = bookingRepository.findById(request.getId())
    //             .orElseThrow(() -> new RuntimeException("Booking Not Found."));

    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String currentUsername = authentication.getName();
    //     checkAuthorization(booking, authentication, currentUsername, "ROLE_RECEPTIONIST");

    //     booking.setSprayersId(request.getSprayersId());
    //     booking.setCropType(request.getCropType());
    //     booking.setFarmlandArea(request.getFarmlandArea());
    //     booking.setTotalCost(request.getFarmlandArea() * 30000);
    //     bookingRepository.save(booking);

    //     return new BookingResponse(booking);
    // }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    private TimeSlotDTO checkOrCreateTimeSlot(LocalDate date, LocalTime startTime) {
        TimeSlotDTO timeSlotDTO = timeSlotAPI.findByDateAndStartTime(date, startTime);

        // if (optionalTimeSlot.isPresent()) {
        //     return optionalTimeSlot.get();
        // } else {
        //     TimeSlotCreateRequest createRequest = new TimeSlotCreateRequest();
        //     createRequest.setDate(date);
        //     createRequest.setStartTime(startTime);

        //     timeSlotService.createTimeSlot(createRequest);

        //     return timeSlotService.findByDateAndStartTime(date, startTime)
        //             .orElseThrow(() -> new RuntimeException("Failed to create time slot"));
        // }

        if (timeSlotDTO == null) {
        TimeSlotCreateRequest createRequest = new TimeSlotCreateRequest();
        createRequest.setDate(date);
        createRequest.setStartTime(startTime);

        timeSlotAPI.createTimeSlot(createRequest);

        return timeSlotAPI.findByDateAndStartTime(date, startTime);
        }
        return timeSlotDTO;
                // .orElseThrow(() -> new RuntimeException("Failed to create time slot"));
    }

    private Booking createBookingWithStatus(BookingStatus status, BookingCreationRequest request, Long timeslotId,
            Long farmerId, Long receptionistId, List<Long> sprayers) {
        Booking booking = new Booking();
        booking.setFarmerId(farmerId);
        booking.setCropType(request.getCropType());
        booking.setStatus(status);
        booking.setFarmlandArea(request.getFarmlandArea());
        booking.setCreatedTime(request.getCreatedTime());
        booking.setTimeSlotId(timeslotId);
        booking.setTotalCost(booking.getFarmlandArea() * 30000);

        if (status == BookingStatus.CONFIRMED & request.getReceptionistId() != null) {
            booking.setReceptionistId(receptionistId);
        }

        booking = bookingRepository.save(booking);

        return booking;
    }

    // private User getCurrentUser() {
    //     String receptionEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    //     return userService.findByEmail(receptionEmail)
    //             .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));
    // }

    // private String getCurrentUserEmail() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
    //     Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
    //     return jwt.getSubject();
    // }

    // private void checkAuthorization(Booking booking, Authentication authentication, String currentUsername, String role)
    //         throws AccessDeniedException {
    //     boolean isAuthorized = booking.getUser().getEmail().equals(currentUsername) ||
    //             authentication.getAuthorities().stream()
    //                     .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));

    //     if (!isAuthorized) {
    //         throw new AccessDeniedException("You do not have permission to perform this action.");
    //     }
    // }

    private ListResponse<BookingResponse> createListResponse(Page<Booking> bookingPage,
            List<BookingResponse> bookingResponses) {
        ListResponse<BookingResponse> listResponse = new ListResponse<>();
        listResponse.setContent(bookingResponses);
        listResponse.setPageNo(bookingPage.getNumber());
        listResponse.setPageSize(bookingPage.getSize());
        listResponse.setTotalPages(bookingPage.getTotalPages());
        listResponse.setTotalSize(bookingPage.getTotalElements());
        listResponse.setLast(bookingPage.isLast());
        return listResponse;
    }
}