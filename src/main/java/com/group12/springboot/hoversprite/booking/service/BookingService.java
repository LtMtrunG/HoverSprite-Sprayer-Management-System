package com.group12.springboot.hoversprite.booking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import com.group12.springboot.hoversprite.booking.*;
import com.group12.springboot.hoversprite.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.springboot.hoversprite.booking.entity.Booking;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.booking.repository.BookingRepository;
import com.group12.springboot.hoversprite.common.ListResponse;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.timeslot.TimeSlotAPI;
import com.group12.springboot.hoversprite.timeslot.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.timeslot.TimeSlotDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService implements BookingAPI {
    private final TimeSlotAPI timeSlotAPI;
    private final UserAPI userAPI;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EmailService emailService;

    @Override
    @PreAuthorize("hasRole('FARMER')")
    public BookingResponse createPendingBooking(BookingCreationRequest request) {
        TimeSlotDTO timeSlotDTO = checkOrCreateTimeSlot(request.getDate(), request.getStartTime());

        Long farmerId = getCurrentUserId();
        FarmerDTO farmerDTO = userAPI.findFarmerById(farmerId);
        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
        }

        if (!timeSlotAPI.isAvailable(timeSlotDTO)) {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        timeSlotAPI.bookSession(timeSlotDTO);

        Booking booking = createBookingWithStatus(BookingStatus.PENDING, request, timeSlotDTO.getId(),
                farmerDTO.getId(), null, Collections.emptyList());
        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse createConfirmedBooking(BookingCreationRequest request) {

        TimeSlotDTO timeSlotDTO = checkOrCreateTimeSlot(request.getDate(), request.getStartTime());

        FarmerDTO farmerDTO = userAPI.findFarmerById(request.getFarmerId());

        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
        }

        Long receptionistId = getCurrentUserId();
        ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(receptionistId);

        if (receptionistDTO == null) {
            throw new CustomException(ErrorCode.RECEPTIONIST_NOT_EXIST);
        }

        if (!timeSlotAPI.isAvailable(timeSlotDTO)) {
            throw new CustomException(ErrorCode.SESSION_NOT_AVAILABLE);
        }

        timeSlotAPI.bookSession(timeSlotDTO);

        Booking booking = createBookingWithStatus(BookingStatus.CONFIRMED, request, timeSlotDTO.getId(),
                farmerDTO.getId(), receptionistDTO.getId(),
                Collections.emptyList());

        emailService.sendEmail(booking);

        bookingRepository.save(booking);

        autoAssignToAllUnAssignedBooking();

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

        TimeSlotDTO timeSlotDTO = timeSlotAPI.findById(booking.getTimeSlotId());
        if (timeSlotDTO == null) {
            throw new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS);
        }

        Long receptionistId = getCurrentUserId();
        ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(receptionistId);

        if (receptionistDTO == null) {
            throw new CustomException(ErrorCode.RECEPTIONIST_NOT_EXIST);
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setReceptionistId(receptionistDTO.getId());
        bookingRepository.save(booking);

        emailService.sendEmail(booking);

        autoAssignToAllUnAssignedBooking();

        return new BookingResponse(booking);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public BookingResponse assignSprayers(BookingAssignRequest request) {

        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        long receptionistId = getCurrentUserId();
        if (receptionistId != booking.getReceptionistId()) {
            throw new CustomException(ErrorCode.RECEPTIONIST_NOT_RESPONSIBLE);
        }

        if (request.getSprayersId() == null) {
            throw new CustomException(ErrorCode.SPRAYER_EMPTY);
        }

        if (request.getSprayersId().size() > 2) {
            throw new CustomException(ErrorCode.SPRAYER_EXCEED);
        }

        List<SprayerDTO> sprayersList = new ArrayList<>();
        Set<Long> seenSprayerIds = new HashSet<>();

        // Loop through the sprayer IDs in the request
        for (Long sprayerId : request.getSprayersId()) {
            if (!seenSprayerIds.contains(sprayerId)) {
                // Mark this sprayer ID as seen
                seenSprayerIds.add(sprayerId);

                // Check if the sprayer exists
                SprayerDTO sprayerDTO = userAPI.findSprayerById(sprayerId);
                if (sprayerDTO == null) {
                    throw new CustomException(ErrorCode.SPRAYER_NOT_EXIST);
                }

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

        TimeSlotDTO timeSlotDTO = timeSlotAPI.findById(booking.getTimeSlotId());
        if (timeSlotDTO == null) {
            throw new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS);
        }

        booking.setSprayersId(request.getSprayersId());

        booking.setStatus(BookingStatus.ASSIGNED);

        // Get the existing list of booked sprayer IDs
        List<Long> bookedSprayersId = timeSlotDTO.getBookedSprayersId();

        // Check if the list is null, and if so, initialize it
        if (bookedSprayersId == null) {
            bookedSprayersId = new ArrayList<>();
        }

        // Add the new sprayer IDs from the request to the list
        bookedSprayersId.addAll(request.getSprayersId());

        // Set the updated list back to timeSlotDTO
        timeSlotAPI.setBookedSprayersId(timeSlotDTO.getId(),bookedSprayersId);
        bookingRepository.save(booking);

        emailService.sendEmail(booking);

        return new BookingResponse(booking);
    }

    private boolean checkSprayersAvailability(List<Long> sprayersId, Long timeSlotId) {
        TimeSlotDTO timeSlotDTO = timeSlotAPI.findById(timeSlotId);
        if (timeSlotDTO == null) {
            throw new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS);
        }

        if (timeSlotDTO.getBookedSprayersId() == null) {
            return true;
        }

        // Check if any sprayer ID from the input list is already booked
        for (Long sprayerId : sprayersId) {
            if (timeSlotDTO.getBookedSprayersId().contains(sprayerId)) {
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
            if (sprayer.getExpertise().toString().equals("APPRENTICE")) {
                apprentice_lv++;
            } else if (sprayer.getExpertise().toString().equals("ADEPT")) {
                adept_lv++;
            } else {
                expert_lv++;
            }
        }

        return ((expert_lv == 1 && apprentice_lv == 0 && adept_lv == 0) || (adept_lv == 2) ||
                (apprentice_lv == 1 && expert_lv == 1) || (apprentice_lv == 1 && adept_lv == 1));
    }

    @Override
    @PreAuthorize("hasRole('SPRAYER')")
    public BookingResponse inProgressBooking(BookingInProgressRequest request) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        if (booking.getStatus() != BookingStatus.ASSIGNED && booking.getStatus() != BookingStatus.IN_PROGRESS_1_2) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        Long sprayerId = getCurrentUserId();

        if (!booking.getSprayersId().contains(sprayerId)) {
            throw new CustomException(ErrorCode.SPRAYER_NOT_ASSIGNED);
        }

        SprayerDTO sprayerDTO = userAPI.findSprayerById(sprayerId);
        if (sprayerDTO == null) {
            throw new CustomException(ErrorCode.SPRAYER_NOT_EXIST);
        }

        // Add the sprayer to the list of those who have confirmed in progress
        if (booking.getInProgressSprayerIds() == null) {
            booking.setInProgressSprayerIds(new ArrayList<>());
        }

        if (!booking.getInProgressSprayerIds().contains(sprayerId)) {
            booking.getInProgressSprayerIds().add(sprayerId);
        } else {
            throw new CustomException(ErrorCode.SPRAYER_ALREADY_IN_PROGRESS);
        }

        // Check if all sprayers have confirmed in progress
        if (booking.getInProgressSprayerIds().size() == booking.getSprayersId().size()) {
            if (booking.getSprayersId().size() == 1) {
                booking.setStatus(BookingStatus.IN_PROGRESS_1_1);
            } else {
                booking.setStatus(BookingStatus.IN_PROGRESS_2_2);
            }
            emailService.sendEmail(booking);
        } else {
            booking.setStatus(BookingStatus.IN_PROGRESS_1_2);
        }

        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @Override
    @PreAuthorize("hasRole('FARMER')")
    public BookingResponse completeBookingByFarmer(BookingCompleteRequest request) {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Booking Not Found."));

        long farmerId = getCurrentUserId();
        FarmerDTO farmerDTO = userAPI.findFarmerById(farmerId);
        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
        }

        if (booking.getFarmerId() != farmerId) {
            throw new CustomException(ErrorCode.FARMER_NOT_OWNED);
        }

        if (booking.getStatus() != BookingStatus.IN_PROGRESS_2_2 && booking.getStatus() != BookingStatus.IN_PROGRESS_1_1) {
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        booking.setStatus(BookingStatus.COMPLETED_BY_FARMER);
        bookingRepository.save(booking);

        return new BookingResponse(booking);
    }

    @PreAuthorize("hasRole('SPRAYER')")
    public BookingResponse completeBookingBySprayer(BookingCompleteRequest
                                                            request) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        Long sprayerId = getCurrentUserId();

        if (!booking.getSprayersId().contains(sprayerId)) {
            throw new CustomException(ErrorCode.SPRAYER_NOT_ASSIGNED);
        }

        SprayerDTO sprayerDTO = userAPI.findSprayerById(sprayerId);
        if (sprayerDTO == null) {
            throw new CustomException(ErrorCode.SPRAYER_NOT_EXIST);
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        emailService.sendEmail(booking);

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
        Long farmerId = getCurrentUserId();

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
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
        return Long.parseLong(jwt.getSubject());
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

        TimeSlotDTO timeSlotDTO = timeSlotAPI.findById(request.getTimeSlotId());
        if (timeSlotDTO == null) {
            throw new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS);
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<SprayerDTO> availableSprayersPage = userAPI.getAvailableSprayers(timeSlotDTO.getBookedSprayersId(), pageable);

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

    @Override
    public boolean isBookingExist(Long bookingId) {
        return bookingRepository.existsById(bookingId);
    }

    @Override
    public boolean doesFarmerOwnBooking(Long bookingId, Long farmerId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            return false;  // If the booking does not exist, return false
        }

        return booking.getFarmerId().equals(farmerId);  // Check if the booking's farmer matches the given farmerId
    }

    @Override
    public void saveFeedbackToBooking(Long bookingId, Long feedbackId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        booking.setFeedbackId(feedbackId);
        bookingRepository.save(booking);
    }

    @Override
    public boolean doesBookingHaveFeedback(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        return (booking.getFeedbackId() != null);
    }

    @Override
    public boolean hasPermissionOrNot(Long feedbackId) {
        Booking booking = bookingRepository.findByFeedbackId(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_EXISTS));

        long userId = getCurrentUserId();

        if (booking.getFarmerId() == userId) {
            return true;
        } else {
            if (booking.getSprayersId().contains(userId)) {
                return true;
            } else {
                ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(userId);
                return receptionistDTO != null;
            }
        }
    }

    public void autoAssignToAllUnAssignedBooking() {
        List<Booking> unAssignedBooking = bookingRepository.findByStatus(BookingStatus.CONFIRMED);
        if (unAssignedBooking == null) {
            return;
        }
        for (Booking booking : unAssignedBooking) {
            TimeSlotDTO timeSlotDTO = timeSlotAPI.findById(booking.getTimeSlotId());
            if (timeSlotDTO == null) {
                throw new RuntimeException();
            }
            autoAssign(booking, timeSlotDTO);
        }
    }

    public void autoAssign(Booking booking, TimeSlotDTO timeSlotDTO) {
        List<TimeSlotDTO> timeSlotDTOList = timeSlotAPI.getTimeSlotByWeek(timeSlotDTO.getDate());
        List<SprayerWeeklyAssignDTO> weeklyAssignDTOs = calculateSprayerBooking(timeSlotDTOList);
        if (timeSlotDTO.getBookedSprayersId() != null) {
            for (Long sprayerId : timeSlotDTO.getBookedSprayersId()) {
                // Remove the SprayerWeeklyAssignDTO with the matching id
                weeklyAssignDTOs.removeIf(sprayer -> sprayer.getId().equals(sprayerId));
            }
        }

        List<Long> selectedSprayerIds = new ArrayList<>();

        selectedSprayerIds.add(weeklyAssignDTOs.getFirst().getId());

        if (weeklyAssignDTOs.getFirst().getExpertise().toString().equals("APPRENTICE")) {
            for (int i = 1; i < weeklyAssignDTOs.size(); i++) {
                SprayerWeeklyAssignDTO sprayer = weeklyAssignDTOs.get(i);
                if (sprayer.getExpertise().toString().equals("ADEPT") || sprayer.getExpertise().toString().equals("EXPERT")) {
                    selectedSprayerIds.add(weeklyAssignDTOs.get(i).getId());
                    break;
                }
            }
        } else if (weeklyAssignDTOs.getFirst().getExpertise().toString().equals("ADEPT")) {
            for (int i = 1; i < weeklyAssignDTOs.size(); i++) {
                SprayerWeeklyAssignDTO sprayer = weeklyAssignDTOs.get(i);
                if (sprayer.getExpertise().toString().equals("ADEPT") || sprayer.getExpertise().toString().equals("APPRENTICE")) {
                    selectedSprayerIds.add(weeklyAssignDTOs.get(i).getId());
                    break;
                }
            }
        }

//        System.out.println(weeklyAssignDTOs);

        if ((selectedSprayerIds.size() == 1 && weeklyAssignDTOs.getFirst().getExpertise().toString().equals("EXPERT")) ||
                selectedSprayerIds.size() == 2) {
            booking.setSprayersId(selectedSprayerIds);
            booking.setStatus(BookingStatus.ASSIGNED);
            bookingRepository.save(booking);
            timeSlotAPI.setBookedSprayersId(timeSlotDTO.getId(), selectedSprayerIds);
            emailService.sendEmail(booking);
        } else if (selectedSprayerIds.size() == 1 && weeklyAssignDTOs.get(1).getExpertise().toString().equals("EXPERT")) {
            selectedSprayerIds.clear();
            selectedSprayerIds.add(weeklyAssignDTOs.get(1).getId());
            booking.setSprayersId(selectedSprayerIds);
            booking.setStatus(BookingStatus.ASSIGNED);
            bookingRepository.save(booking);
            timeSlotAPI.setBookedSprayersId(timeSlotDTO.getId(), selectedSprayerIds);
            emailService.sendEmail(booking);
        }
    }

    private List<SprayerWeeklyAssignDTO> calculateSprayerBooking(List<TimeSlotDTO> timeSlotDTOList) {
        // Map to store sprayer ID and their frequency
        Map<Long, SprayerWeeklyAssignDTO> sprayerMap = new HashMap<>();

        // Fetch all available sprayers
        List<SprayerDTO> sprayerDTOList = userAPI.getAvailableSprayers(Collections.emptyList());

        // Initialize the map with all sprayers and default booking_assign value of 0
        for (SprayerDTO sprayerDTO : sprayerDTOList) {
            sprayerMap.put(sprayerDTO.getId(), new SprayerWeeklyAssignDTO(
                    sprayerDTO.getId(),
                    sprayerDTO.getExpertise(),
                    0 // Default booking_assign value
            ));
        }

        // Iterate through each TimeSlotDTO
        for (TimeSlotDTO timeSlot : timeSlotDTOList) {
            List<Long> sprayersIdList = timeSlot.getBookedSprayersId();

            // Iterate through each sprayer ID in the current TimeSlotDTO
            if (sprayersIdList != null) {
                for (Long sprayerId : sprayersIdList) {
                    SprayerDTO sprayerDTO = userAPI.findSprayerById(sprayerId);
                    if (sprayerDTO == null) {
                        throw new RuntimeException();
                    }
                    SprayerWeeklyAssignDTO sprayer = sprayerMap.get(sprayerId);
                    sprayerMap.put(sprayerId, new SprayerWeeklyAssignDTO(sprayerId, sprayerDTO.getExpertise(), sprayer.getBooking_assign() + 1));
                }
            }
        }

        return sprayerMap.values()
                .stream()
                .sorted(Comparator
                        .comparingInt(SprayerWeeklyAssignDTO::getBooking_assign)               // First sort by frequency
                        .thenComparing(SprayerWeeklyAssignDTO::getExpertise))             // Then sort by expertise
                .collect(Collectors.toList());
    }
}