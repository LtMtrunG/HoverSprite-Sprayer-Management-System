package com.group12.springboot.hoversprite.booking.service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
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

import com.group12.springboot.hoversprite.booking.AvailableSprayersRequest;
import com.group12.springboot.hoversprite.booking.AvailableSprayersResponse;
import com.group12.springboot.hoversprite.booking.BookingAPI;
import com.group12.springboot.hoversprite.booking.BookingAssignRequest;
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
import com.group12.springboot.hoversprite.user.SprayerDTO;
import com.group12.springboot.hoversprite.user.UserAPI;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService implements BookingAPI {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EmailService emailService;
    private final TimeSlotAPI timeSlotAPI;
    private final UserAPI userAPI;

    @Override
    @PreAuthorize("hasRole('FARMER')")
    public BookingResponse createPendingBooking(BookingCreationRequest request) {
        TimeSlotDTO timeSlotDTO = checkOrCreateTimeSlot(request.getDate(), request.getStartTime());

        FarmerDTO farmerDTO = userAPI.findFarmerById(request.getFarmerId());

        if (!timeSlotAPI.isAvailable(timeSlotDTO)) {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        timeSlotAPI.bookSession(timeSlotDTO);

        Booking booking = createBookingWithStatus(BookingStatus.PENDING, request, timeSlotDTO.getId(),
                farmerDTO.getId(), null, Collections.EMPTY_LIST);
        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse createConfirmedBooking(BookingCreationRequest request) {

        TimeSlotDTO timeSlotDTO = checkOrCreateTimeSlot(request.getDate(), request.getStartTime());

        FarmerDTO farmerDTO = userAPI.findFarmerById(request.getFarmerId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
        Long receptionistId = Long.parseLong(jwt.getSubject());
        ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(receptionistId);

        if (!timeSlotAPI.isAvailable(timeSlotDTO)) {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        timeSlotAPI.bookSession(timeSlotDTO);

        Booking booking = createBookingWithStatus(BookingStatus.CONFIRMED, request, timeSlotDTO.getId(),
                farmerDTO.getId(), receptionistDTO.getId(),
                Collections.EMPTY_LIST);

        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse cancelBooking(BookingCancelRequest request) {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        timeSlotAPI.cancelSession(booking.getTimeSlotId());

        emailService.sendEmail(booking);

        return new BookingResponse(booking);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse confirmBooking(BookingConfirmationRequest request) {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
        Long receptionistId = Long.parseLong(jwt.getSubject());
        ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(receptionistId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setReceptionistId(receptionistDTO.getId());
        bookingRepository.save(booking);

        emailService.sendEmail(booking);

        return new BookingResponse(booking);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse assignSprayers(BookingAssignRequest request) {

        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        if (request.getSprayersId() == null) {
            throw new CustomException(ErrorCode.SPRAYER_EMPTY);
        }

        if (request.getSprayersId().size() > 2) {
            throw new CustomException(ErrorCode.SPRAYER_EXCEED);
        }

        List<SprayerDTO> sprayersList = new ArrayList<SprayerDTO>();
        Set<Long> seenSprayerIds = new HashSet<>();

        // Loop through the sprayer IDs in the request
        for (Long sprayerId : request.getSprayersId()) {
            if (!seenSprayerIds.contains(sprayerId)) {
                // Mark this sprayer ID as seen
                seenSprayerIds.add(sprayerId);

                // Check if the sprayer exists
                SprayerDTO sprayerDTO = userAPI.findSprayerById(sprayerId);

                // Assuming the availability is already checked, add the sprayer to the list
                sprayersList.add(sprayerDTO);
            } else {
                // Handle the case where a duplicate is found, e.g., logging or skipping
                throw new CustomException(ErrorCode.SPRAYER_DUPLICATE);
            }
        }

        if (!checkSprayersAvailability(request.getSprayersId(), booking.getTimeSlotId())) {
            throw new CustomException(ErrorCode.SPRAYER_NOT_AVAILABLE);
        }

        if (!checkSprayersExpertises(sprayersList)) {
            throw new CustomException(ErrorCode.SPRAYER_EXPERTISE_NOT_MEET_REQUIREMENTS);
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        booking.setSprayersId(request.getSprayersId());

        booking.setStatus(BookingStatus.ASSIGNED);

        bookingRepository.save(booking);

        emailService.sendEmail(booking);

        return new BookingResponse(booking);
    }

    private boolean checkSprayersAvailability(List<Long> sprayersId, Long timeSlotId) {
        List<Long> bookedSprayersId = getSprayersIdFromBooking(timeSlotId);

        // Check if any sprayer ID from the input list is already booked
        for (Long sprayerId : sprayersId) {
            if (bookedSprayersId.contains(sprayerId)) {
                return false; // If any sprayer is booked, return false
            }
        }

        return true; // If none are booked, return true
    }

    private boolean checkSprayersExpertises(List<SprayerDTO> sprayersList) {

        int apprentice_lv = 0;
        int adept_lv = 0;
        int expert_lv = 0;

        for (SprayerDTO sprayer : sprayersList) {
            if (sprayer.getExpertise().toString() == "APPRENTICE") {
                apprentice_lv++;
            } else if (sprayer.getExpertise().toString() == "ADEPT") {
                adept_lv++;
            } else {
                expert_lv++;
            }
        }

        return ((expert_lv == 1 && apprentice_lv == 0 && adept_lv == 0) || (adept_lv == 2) ||
                (apprentice_lv == 1 && expert_lv == 1) || (apprentice_lv == 1 && adept_lv == 1));
    }

    // public BookingResponse inProgressBooking(BookingInProgressRequest request)
    // throws AccessDeniedException {
    // Booking booking = bookingRepository.findById(request.getId())
    // .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

    // if (booking.getStatus() != BookingStatus.ASSIGNED) {
    // throw new CustomException(ErrorCode.INVALID_ACTION);
    // }

    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // String currentUsername = authentication.getName();
    // checkAuthorization(booking, authentication, currentUsername, "ROLE_SPRAYER");

    // User currentUser = userService.findByEmail(currentUsername)
    // .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

    // booking.setStatus(BookingStatus.IN_PROGRESS);
    // bookingRepository.save(booking);

    // return new BookingResponse(booking);
    // }

    // public BookingResponse completeBookingBySprayer(BookingCompleteRequest
    // request) throws AccessDeniedException {
    // Booking booking = bookingRepository.findById(request.getId())
    // .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

    // if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
    // throw new CustomException(ErrorCode.INVALID_ACTION);
    // }

    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // currentUsername = authentication.getName();
    // checkAuthorization(booking, authentication, currentUsername, "ROLE_SPRAYER");

    // User currentUser = userService.findByEmail(currentUsername)
    // .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

    // booking.setStatus(BookingStatus.COMPLETED_BY_SPRAYER);
    // bookingRepository.save(booking);

    // return new BookingResponse(booking);
    // }

    @Override
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

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ListResponse<BookingResponse> getBookings(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Booking> bookingPage = bookingRepository.findAllOrderByStatus(pageable);

        List<BookingResponse> bookingResponses = bookingPage.getContent().stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());

        return createListResponse(bookingPage, bookingResponses);
    }

    @Override
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

    // public BookingResponse getBookingById(Long bookingId) throws
    // AccessDeniedException {
    // Booking booking = bookingRepository.findById(bookingId)
    // .orElseThrow(() -> new RuntimeException("Booking Not Found."));

    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // String currentUsername = authentication.getName();
    // checkAuthorization(booking, authentication, currentUsername,
    // "ROLE_RECEPTIONIST");

    // return new BookingResponse(booking);
    // }

    // public BookingResponse updateBooking(BookingUpdateRequest request) throws
    // AccessDeniedException {
    // Booking booking = bookingRepository.findById(request.getId())
    // .orElseThrow(() -> new RuntimeException("Booking Not Found."));

    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // String currentUsername = authentication.getName();
    // checkAuthorization(booking, authentication, currentUsername,
    // "ROLE_RECEPTIONIST");

    // booking.setSprayersId(request.getSprayersId());
    // booking.setCropType(request.getCropType());
    // booking.setFarmlandArea(request.getFarmlandArea());
    // booking.setTotalCost(request.getFarmlandArea() * 30000);
    // bookingRepository.save(booking);

    // return new BookingResponse(booking);
    // }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    private TimeSlotDTO checkOrCreateTimeSlot(LocalDate date, LocalTime startTime) {
        Optional<TimeSlotDTO> timeSlotDTO = timeSlotAPI.findByDateAndStartTime(date, startTime);

        // if (optionalTimeSlot.isPresent()) {
        // return optionalTimeSlot.get();
        // } else {
        // TimeSlotCreateRequest createRequest = new TimeSlotCreateRequest();
        // createRequest.setDate(date);
        // createRequest.setStartTime(startTime);

        // timeSlotService.createTimeSlot(createRequest);

        // return timeSlotService.findByDateAndStartTime(date, startTime)
        // .orElseThrow(() -> new RuntimeException("Failed to create time slot"));
        // }

        if (timeSlotDTO.isPresent()) {
            return timeSlotDTO.get();
        } else {
            TimeSlotCreateRequest createRequest = new TimeSlotCreateRequest();
            createRequest.setDate(date);
            createRequest.setStartTime(startTime);

            timeSlotAPI.createTimeSlot(createRequest);

            return timeSlotAPI.findByDateAndStartTime(date, startTime)
                    .orElseThrow(() -> new RuntimeException("Failed to create time slot"));
        }

        // if (timeSlotDTO == null) {
        // TimeSlotCreateRequest createRequest = new TimeSlotCreateRequest();
        // createRequest.setDate(date);
        // createRequest.setStartTime(startTime);

        // timeSlotAPI.createTimeSlot(createRequest);

        // return timeSlotAPI.findByDateAndStartTime(date, startTime);
        // }
        // return timeSlotDTO;
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

        if (status == BookingStatus.CONFIRMED & receptionistId != null) {
            booking.setReceptionistId(receptionistId);
        }

        booking = bookingRepository.save(booking);

        return booking;
    }

    // private User getCurrentUser() {
    // String receptionEmail =
    // SecurityContextHolder.getContext().getAuthentication().getName();
    // return userService.findByEmail(receptionEmail)
    // .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));
    // }

    // private String getCurrentUserEmail() {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken)
    // authentication;
    // Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
    // return jwt.getSubject();
    // }

    // private void checkAuthorization(Booking booking, Authentication
    // authentication, String currentUsername, String role)
    // throws AccessDeniedException {
    // boolean isAuthorized = booking.getUser().getEmail().equals(currentUsername)
    // ||
    // authentication.getAuthorities().stream()
    // .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));

    // if (!isAuthorized) {
    // throw new AccessDeniedException("You do not have permission to perform this
    // action.");
    // }
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

    private List<Long> getSprayersIdFromBooking(Long timeSlotId) {
        List<Booking> bookingList = bookingRepository.findByTimeSlotId(timeSlotId);
        List<Long> bookedSprayersId = new ArrayList<>();
        if (bookingList.isEmpty()) {
            return Collections.emptyList();
        }
        for (Booking booking : bookingList) {
            List<Long> sprayersId = booking.getSprayersId();
            if (sprayersId != null) {
                bookedSprayersId.addAll(sprayersId);
            }
        }

        return bookedSprayersId;
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ListResponse<AvailableSprayersResponse> getAvailableSprayersByTimeSlot(int pageNo, int pageSize,
                                                                                  AvailableSprayersRequest request) {

        List<Long> bookedSprayersId = getSprayersIdFromBooking(request.getTimeSlotId());
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<SprayerDTO> availableSprayersPage = userAPI.getAvailableSprayers(bookedSprayersId, pageable);

        // Convert SprayerDTO to AvailableSprayersResponse
        List<AvailableSprayersResponse> availableSprayerResponses = availableSprayersPage.getContent().stream()
                .map(sprayer -> new AvailableSprayersResponse(sprayer))
                .collect(Collectors.toList());

        ListResponse<AvailableSprayersResponse> listResponse = new ListResponse<>();
        listResponse.setContent(availableSprayerResponses);
        listResponse.setPageNo(availableSprayersPage.getNumber());
        listResponse.setPageSize(availableSprayersPage.getSize());
        listResponse.setTotalPages(availableSprayersPage.getTotalPages());
        listResponse.setTotalSize(availableSprayersPage.getTotalElements());
        listResponse.setLast(availableSprayersPage.isLast());

        return listResponse;
    }
}