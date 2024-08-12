package com.group12.springboot.hoversprite.booking.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.group12.springboot.hoversprite.booking.entity.Booking;
import com.group12.springboot.hoversprite.timeslot.TimeSlotAPI;
import com.group12.springboot.hoversprite.timeslot.TimeSlotDTO;
import com.group12.springboot.hoversprite.user.FarmerDTO;
import com.group12.springboot.hoversprite.user.UserAPI;
import com.ibm.icu.util.ChineseCalendar;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    private final TimeSlotAPI timeSlotAPI;
    private final UserAPI userAPI;

    @Async
    public void sendEmail(Long to, Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        String subject = "Booking Confirmation: HoverSprite Service - Booking ID " + booking.getId();
        String body = generateEmailBody(booking);
        
        FarmerDTO farmerDTO = userAPI.findFarmerById(to);
        message.setTo(farmerDTO.getEmail());
        message.setFrom("s3927071@rmit.edu.vn");
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    private String generateEmailBody(Booking booking) {
        // DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        TimeSlotDTO timeSlotDTO = timeSlotAPI.findById(booking.getTimeSlotId());
        FarmerDTO farmerDTO = userAPI.findFarmerById(booking.getFarmerId());
        String gregorianDate = timeSlotDTO.getDate().toString();


        // Convert LocalDateto Chinese Lunar Date
        LocalDateTime dateTime = timeSlotDTO.getDate().atStartOfDay();
        ChineseCalendar chineseCalendar = new ChineseCalendar();
        chineseCalendar.setTimeInMillis(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        int lunarYear = chineseCalendar.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
        int lunarMonth = chineseCalendar.get(ChineseCalendar.MONTH) + 1; // 0-based, so we add 1
        int lunarDay = chineseCalendar.get(ChineseCalendar.DAY_OF_MONTH);

        String lunarDateString = String.format("%d-%02d-%02d", lunarYear, lunarMonth, lunarDay);

        return String.format(
            "Dear %s,\n\n" +
            "Thank you for choosing HoverSprite Service. We are pleased to confirm your booking.\n\n" +
            "Booking Details:\n" +
            "Booking ID: %d\n" +
            "Date (Gregorian): %s\n" +
            "Date (Lunar): %s\n" +
            "Time Slot: %s\n" +
            "Location: %s\n" +
            "Farmland Size: %.2f decares\n" +
            "Total Cost: %.2fVND\n\n" +
            "We appreciate your trust and choice in using our service.\n\n" +
            "Best regards,\n" +
            "HoverSprite Team",
            farmerDTO.getFullName(),
            booking.getId(),
            gregorianDate,
            lunarDateString,
            timeSlotDTO.getStartTime(),
            farmerDTO.getAddress(),
            booking.getFarmlandArea(),
            booking.getTotalCost()
        );
    }
}