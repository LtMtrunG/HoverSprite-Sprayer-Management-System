package com.group12.springboot.hoversprite.email;

import com.group12.springboot.hoversprite.booking.BookingDTO;

public interface EmailAPI {
    public void sendBookingEmail(BookingDTO booking);

    public void sendForgetPassword(String email, String newPassword);
}
