package com.group12.springboot.hoversprite.booking;

import com.group12.springboot.hoversprite.common.ListResponse;
import org.springframework.security.access.AccessDeniedException;

public interface BookingAPI {

    public BookingResponse createPendingBooking(BookingCreationRequest request);

    public BookingResponse createConfirmedBooking(BookingCreationRequest request);

    public BookingResponse cancelBooking(BookingCancelRequest request);

    public BookingResponse confirmBooking(BookingConfirmationRequest request);

    public BookingResponse completeBookingByFarmer(BookingCompleteRequest request);

    public ListResponse<BookingResponse> getBookings(int pageNo, int pageSize);

    public ListResponse<BookingResponse> getMyBookings(int pageNo, int pageSize);

    public void deleteBooking(Long bookingId);

    public ListResponse<AvailableSprayersResponse> getAvailableSprayersByTimeSlot(int pageNo, int pageSize, AvailableSprayersRequest request);

    public BookingResponse assignSprayers(BookingAssignRequest request);

    public BookingResponse inProgressBooking(BookingInProgressRequest request) throws AccessDeniedException;
}