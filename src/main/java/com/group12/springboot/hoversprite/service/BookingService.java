package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.request.booking.*;
import com.group12.springboot.hoversprite.dataTransferObject.request.timeslot.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.BookingResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.ListResponse;
import com.group12.springboot.hoversprite.entity.*;
import com.group12.springboot.hoversprite.entity.enums.ActionType;
import com.group12.springboot.hoversprite.entity.enums.BookingStatus;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.repository.BookingRepository;
import com.group12.springboot.hoversprite.repository.NotificationRepository;
import com.group12.springboot.hoversprite.repository.TimeSlotRepository;
import com.group12.springboot.hoversprite.repository.UserRepository;
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

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    private TimeSlotService timeSlotService;
    private UserService userService;
    private NotificationService notificationService;

    @PreAuthorize("hasRole('FARMER')")
    public BookingResponse createPendingBooking(BookingCreationRequest request) {
        TimeSlot timeSlot = checkOrCreateTimeSlot(request.getDate(), request.getStartTime());

        if (!timeSlot.isAvailable()) {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        timeSlot.bookSession();
        timeSlotService.save(timeSlot);

        Action action = createAction(request.getUser(), ActionType.CREATE_BY.name());
        Booking booking = createBookingWithStatus(BookingStatus.PENDING, request, timeSlot, action);
        bookingRepository.save(booking);

        notificationService.createNotification(request.getUser(), action);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse createConfirmedBooking(BookingCreationRequest request) {
        TimeSlot timeSlot = checkOrCreateTimeSlot(request.getDate(), request.getStartTime());

        if (!timeSlot.isAvailable()) {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        timeSlot.bookSession();
        timeSlotService.save(timeSlot);

        User receptionist = request.getReceptionist();

        Action createAction = createAction(receptionist, ActionType.CREATE_BY.name());
        Booking booking = createBookingWithStatus(BookingStatus.CONFIRMED, request, timeSlot, createAction);

        Action confirmAction = createAction(receptionist, ActionType.CONFIRMED_BY.name());
        booking.addAction(confirmAction);
        bookingRepository.save(booking);

        notificationService.createNotification(receptionist, createAction);

        return new BookingResponse(booking);
    }


    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse cancelBooking(BookingCancelRequest request) {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        User receptionist = getCurrentUser();

        Action action = createAction(receptionist, ActionType.CANCELLED_BY.name());
        booking.addAction(action);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        notificationService.createNotification(booking.getUser(), action);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse confirmBooking(BookingConfirmationRequest request) {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        User receptionist = getCurrentUser();

        Action action = createAction(receptionist, ActionType.CONFIRMED_BY.name());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.addAction(action);
        bookingRepository.save(booking);

        notificationService.createNotification(booking.getUser(), action);

        return new BookingResponse(booking);
    }

    public BookingResponse inProgressBooking(BookingInProgressRequest request) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        if (booking.getStatus() != BookingStatus.ASSIGNED) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        checkAuthorization(booking, authentication, currentUsername, "ROLE_SPRAYER");

        User currentUser = userService.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

        Action action = createAction(currentUser, ActionType.PROCESS_BY.name());
        booking.addAction(action);
        booking.setStatus(BookingStatus.IN_PROGRESS);
        bookingRepository.save(booking);

        notificationService.createNotification(booking.getUser(), action);

        return new BookingResponse(booking);
    }

    public BookingResponse completeBookingBySprayer(BookingCompleteRequest request) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        checkAuthorization(booking, authentication, currentUsername, "ROLE_SPRAYER");

        User currentUser = userService.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

        Action action = createAction(currentUser, ActionType.COMPLETE_BY.name());
        booking.addAction(action);
        booking.setStatus(BookingStatus.COMPLETED_BY_SPRAYER);
        bookingRepository.save(booking);

        notificationService.createNotification(booking.getUser(), action);

        return new BookingResponse(booking);
    }

    @PostAuthorize("booking.user.email == authentication.name")
    public BookingResponse completeBookingByFarmer(BookingCompleteRequest request)  {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Booking Not Found."));

        if (booking.getStatus() != BookingStatus.COMPLETED_BY_SPRAYER) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        User farmer = getCurrentUser();

        Action action = createAction(farmer, ActionType.COMPLETE_BY.name());
        booking.addAction(action);
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        notificationService.createNotification(booking.getUser(), action);

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
        String email = jwt.getSubject();

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Booking> bookingPage = bookingRepository.findByUserEmailOrderByStatus(email, pageable);

        List<BookingResponse> bookingResponses = bookingPage.getContent().stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());

        return createListResponse(bookingPage, bookingResponses);
    }

    public BookingResponse getBookingById(Long bookingId) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking Not Found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        checkAuthorization(booking, authentication, currentUsername,"ROLE_RECEPTIONIST");

        return new BookingResponse(booking);
    }

    public BookingResponse updateBooking(BookingUpdateRequest request) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Booking Not Found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        checkAuthorization(booking, authentication, currentUsername,"ROLE_RECEPTIONIST");

        booking.setSprayers(request.getSprayers());
        booking.setCropType(request.getCropType());
        booking.setFarmlandArea(request.getFarmlandArea());
        booking.setTotalCost(request.getFarmlandArea() * 30000);
        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void deleteBooking(Long bookingId){
        bookingRepository.deleteById(bookingId);
    }

    private TimeSlot checkOrCreateTimeSlot(LocalDate date, LocalTime startTime) {
        Optional<TimeSlot> optionalTimeSlot = timeSlotService.findByDateAndStartTime(date, startTime);

        if (optionalTimeSlot.isPresent()) {
            return optionalTimeSlot.get();
        } else {
            TimeSlotCreateRequest createRequest = new TimeSlotCreateRequest();
            createRequest.setDate(date);
            createRequest.setStartTime(startTime);

            timeSlotService.createTimeSlot(createRequest);

            return timeSlotService.findByDateAndStartTime(date, startTime)
                    .orElseThrow(() -> new RuntimeException("Failed to create time slot"));
        }
    }

    private Booking createBookingWithStatus(BookingStatus status, BookingCreationRequest request, TimeSlot timeslot, Action action){
        Booking booking = new Booking();
        booking.setUser(request.getUser());
        booking.setCropType(request.getCropType());
        booking.setStatus(status);
        booking.setFarmlandArea(request.getFarmlandArea());
        booking.setCreatedTime(request.getCreatedTime());
        booking.setTimeSlot(timeslot);
        booking.setTotalCost(booking.getFarmlandArea() * 30000);
        booking.addAction(action);

        if(booking.getActions() == null){
            booking.setActions(new ArrayList<>());
        }

        if(status == BookingStatus.CONFIRMED & request.getReceptionist() != null){
            booking.setReceptionist(request.getReceptionist());
        }

        booking = bookingRepository.save(booking);

        return booking;
    }

    private User getCurrentUser() {
        String receptionEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(receptionEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
        return jwt.getSubject();
    }

    private Action createAction(User actor, String actionType) {
        Action action = new Action();
        action.setActor(actor);
        action.setActionType(actionType);
        action.setExecutionTime(LocalDateTime.now());
        return action;
    }

    private void checkAuthorization(Booking booking, Authentication authentication, String currentUsername, String role) throws AccessDeniedException {
        boolean isAuthorized = booking.getUser().getEmail().equals(currentUsername) ||
                authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));

        if (!isAuthorized) {
            throw new AccessDeniedException("You do not have permission to perform this action.");
        }
    }

    private ListResponse<BookingResponse> createListResponse(Page<Booking> bookingPage, List<BookingResponse> bookingResponses) {
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
