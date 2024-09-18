package com.group12.springboot.hoversprite.payment.service;

import com.group12.springboot.hoversprite.booking.BookingAPI;
import com.group12.springboot.hoversprite.booking.BookingDTO;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class PaymentService {

    private final BookingAPI bookingAPI;

    @PreAuthorize("hasRole('FARMER')")
    public String payByCard(Long bookingId) {
        BookingDTO bookingDTO = bookingAPI.findBookingById(bookingId);

        if (bookingDTO == null) {
            throw new CustomException(ErrorCode.BOOKING_NOT_EXISTS);
        }

        if (!bookingDTO.getPaymentStatus().name().equals("UNPAID")) {
            throw new CustomException(ErrorCode.BOOKING_PAID);
        }

        bookingAPI.changeStatusToCardAndSave(bookingId);
        return "Pay by card successfully";
    }

    @PreAuthorize("hasRole('SPRAYER')")
    public String payByCash(Long bookingId) {
        BookingDTO bookingDTO = bookingAPI.findBookingById(bookingId);

        if (bookingDTO == null) {
            throw new CustomException(ErrorCode.BOOKING_NOT_EXISTS);
        }

        if (!bookingDTO.getPaymentStatus().name().equals("UNPAID")) {
            throw new CustomException(ErrorCode.BOOKING_PAID);
        }

        bookingAPI.changeStatusToCashAndSave(bookingId);
        return "Pay by card successfully";
    }
}
