package com.group12.springboot.hoversprite.booking;

import com.group12.springboot.hoversprite.common.ListResponse;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

public interface BookingAPI {

    public BookingResponse createPendingBooking(BookingCreationRequest request);

    public BookingResponse createConfirmedBooking(BookingCreationRequest request);

    public BookingResponse cancelBooking(BookingCancelRequest request);

    public BookingResponse confirmBooking(BookingConfirmationRequest request);

    public void deleteBooking(Long bookingId);

    public BookingResponse assignSprayers(BookingAssignRequest request);

    public boolean isBookingExist(Long bookingId);

    public boolean doesFarmerOwnBooking(Long bookingId, Long farmerId);

    public boolean doesBookingHaveFeedback(Long bookingId);

    public boolean hasPermissionOrNot(Long feedbackId);

    public void saveFeedbackToBooking(Long bookingId, Long feedbackId);

    public List<BookingDTO> findIncompleteBookingByFieldId(Long fieldId);

    public BookingDTO findBookingById(Long bookingId);

    public void changeStatusToCardAndSave(Long id);

    public void changeStatusToCashAndSave(Long id);
}