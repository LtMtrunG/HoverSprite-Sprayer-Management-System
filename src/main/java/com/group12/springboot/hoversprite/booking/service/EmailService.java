package com.group12.springboot.hoversprite.booking.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.group12.springboot.hoversprite.booking.entity.Booking;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.timeslot.TimeSlotAPI;
import com.group12.springboot.hoversprite.timeslot.TimeSlotDTO;
import com.group12.springboot.hoversprite.user.FarmerDTO;
import com.group12.springboot.hoversprite.user.SprayerDTO;
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

//    @Async
    public void sendEmail(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        String subject = generateEmailSubject(booking);
        String body = generateEmailBody(booking);

        FarmerDTO farmerDTO = userAPI.findFarmerById(booking.getFarmerId());
        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
        }

        message.setTo(farmerDTO.getEmail());
        message.setFrom("s3927071@rmit.edu.vn");
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        System.out.println("Send successfully");

        if (booking.getStatus() == BookingStatus.ASSIGNED) {
            sendEmailToSprayers(booking);
        }
    }

    private void sendEmailToSprayers(Booking booking) {

        List<SprayerDTO> sprayersList = new ArrayList<SprayerDTO>();

        // Loop through the sprayer IDs in the request
        for (Long sprayerId : booking.getSprayersId()) {
            // Check if the sprayer exists and is available
            SprayerDTO sprayerDTO = userAPI.findSprayerById(sprayerId);
            if (sprayerDTO == null) {
                throw new CustomException(ErrorCode.SPRAYER_NOT_EXIST);
            }
            // Assuming the availability is already checked, add the sprayer to the list
            sprayersList.add(sprayerDTO);
        }

        FarmerDTO farmerDTO = userAPI.findFarmerById(booking.getFarmerId());
        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
        }

        for (SprayerDTO sprayerDTO : sprayersList) {
            String sprayerEmailBody = String.format(
                    "Dear %s,\n\n" +
                            "You have been assigned to a booking with HoverSprite Service. Please find the details of the farmer below:\n\n"
                            +
                            "Farmer Name: %s\n" +
                            "Location: %s\n" +
                            "Phone Number: %s\n\n" +
                            "Please proceed as per the schedule and reach out if you need further information.\n\n" +
                            "Best regards,\n" +
                            "HoverSprite Team",
                    sprayerDTO.getFullName(),
                    farmerDTO.getFullName(),
                    farmerDTO.getAddress(),
                    farmerDTO.getPhoneNumber());

            SimpleMailMessage sprayerMessage = new SimpleMailMessage();
            sprayerMessage.setTo(sprayerDTO.getEmail());
            sprayerMessage.setFrom("s3927071@rmit.edu.vn");
            sprayerMessage.setSubject("Booking Assignment - HoverSprite Service");
            sprayerMessage.setText(sprayerEmailBody);

            mailSender.send(sprayerMessage);
        }
    }

    private String generateEmailSubject(Booking booking) {
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return "Booking Confirmation: HoverSprite Service - Booking ID " + booking.getId();
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return "Booking Cancellation: HoverSprite Service - Booking ID " + booking.getId();
        }

        if (booking.getStatus() == BookingStatus.ASSIGNED) {
            return "Booking Assignment: HoverSprite Service - Booking ID " + booking.getId();
        }

        if (booking.getStatus() == BookingStatus.IN_PROGRESS_1_1 || booking.getStatus() == BookingStatus.IN_PROGRESS_2_2) {
            return "Booking In Progress: HoverSprite Service - Booking ID " + booking.getId();
        }

        return "Booking Completion: HoverSprite Service - Booking ID " + booking.getId();
    }

    private String generateEmailBody(Booking booking) {
        // DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
        // HH:mm");
        TimeSlotDTO timeSlotDTO = timeSlotAPI.findById(booking.getTimeSlotId());

        FarmerDTO farmerDTO = userAPI.findFarmerById(booking.getFarmerId());
        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
        }

        String gregorianDate = timeSlotDTO.getDate().toString();

        // Convert LocalDateto Chinese Lunar Date
        LocalDateTime dateTime = timeSlotDTO.getDate().atStartOfDay();
        ChineseCalendar chineseCalendar = new ChineseCalendar();
        chineseCalendar.setTimeInMillis(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        int lunarYear = chineseCalendar.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
        int lunarMonth = chineseCalendar.get(ChineseCalendar.MONTH) + 1; // 0-based, so we add 1
        int lunarDay = chineseCalendar.get(ChineseCalendar.DAY_OF_MONTH);

        String lunarDateString = String.format("%d-%02d-%02d", lunarYear, lunarMonth, lunarDay);

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return String.format(
                    "Dear %s,\n\n" +
                            "Thank you for choosing HoverSprite Service. We are pleased to confirm your booking, and we are assigning technicians to process your order.\n\n"
                            +
                            "Booking Details:\n" +
                            "Booking ID: %d\n" +
                            "Date (Gregorian): %s\n" +
                            "Date (Lunar): %s\n" +
                            "Time Slot: %s\n" +
                            "Location: %s\n" +
                            "Farmland Size: %.2f decares\n" +
                            "Total Cost: %.2fVND\n\n" +
                            "If you have any questions or need further assistance, please don't hesitate to contact us.\n\n"
                            +
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
                    booking.getTotalCost());
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return String.format(
                    "Dear %s,\n\n" +
                            "We regret to inform you that your booking with HoverSprite Service has been canceled.\n\n"
                            +
                            "Cancellation Details:\n" +
                            "Booking ID: %d\n" +
                            "Date (Gregorian): %s\n" +
                            "Date (Lunar): %s\n" +
                            "Time Slot: %s\n" +
                            "Location: %s\n" +
                            "Farmland Size: %.2f decares\n" +
                            "Total Cost: %.2f VND\n\n" +
                            "If you have any questions or need further assistance, please don't hesitate to contact us.\n\n"
                            +
                            "We apologize for any inconvenience this may cause and appreciate your understanding.\n\n" +
                            "Best regards,\n" +
                            "HoverSprite Team",
                    farmerDTO.getFullName(),
                    booking.getId(),
                    gregorianDate,
                    lunarDateString,
                    timeSlotDTO.getStartTime(),
                    farmerDTO.getAddress(),
                    booking.getFarmlandArea(),
                    booking.getTotalCost());
        }

        if (booking.getStatus() == BookingStatus.ASSIGNED) {

            List<SprayerDTO> sprayersList = new ArrayList<SprayerDTO>();

            // Loop through the sprayer IDs in the request
            for (Long sprayerId : booking.getSprayersId()) {
                // Check if the sprayer exists and is available
                SprayerDTO sprayerDTO = userAPI.findSprayerById(sprayerId);

                // Assuming the availability is already checked, add the sprayer to the list
                sprayersList.add(sprayerDTO);
            }

            // Collect the names of the assigned sprayers
            String sprayerNames = sprayersList.stream()
                    .map(SprayerDTO::getFullName)
                    .collect(Collectors.joining(", "));

            return String.format(
                    "Dear %s,\n\n" +
                            "We are pleased to inform you that your booking with HoverSprite Service is progressing smoothly. The following sprayers have been successfully assigned to your booking: %s.\n\n"
                            +
                            "Booking Details:\n" +
                            "Booking ID: %d\n" +
                            "Date (Gregorian): %s\n" +
                            "Date (Lunar): %s\n" +
                            "Time Slot: %s\n" +
                            "Location: %s\n" +
                            "Farmland Size: %.2f decares\n" +
                            "Total Cost: %.2f VND\n\n" +
                            "Our team will be arriving as scheduled to carry out the service. If you have any special instructions or need further assistance, please don't hesitate to reach out to us.\n\n"
                            +
                            "Thank you for choosing HoverSprite Service.\n\n" +
                            "Best regards,\n" +
                            "HoverSprite Team",
                    farmerDTO.getFullName(),
                    sprayerNames, // Include sprayer names here
                    booking.getId(),
                    gregorianDate,
                    lunarDateString,
                    timeSlotDTO.getStartTime(),
                    farmerDTO.getAddress(),
                    booking.getFarmlandArea(),
                    booking.getTotalCost());
        }

        if (booking.getStatus() == BookingStatus.IN_PROGRESS_1_1 || booking.getStatus() == BookingStatus.IN_PROGRESS_2_2) {
            return String.format(
                    "Dear %s,\n\n" +
                            "We are currently processing your booking with HoverSprite Service. Our team is on-site and the service is in progress.\n\n"
                            +
                            "Booking Details:\n" +
                            "Booking ID: %d\n" +
                            "Date (Gregorian): %s\n" +
                            "Date (Lunar): %s\n" +
                            "Time Slot: %s\n" +
                            "Location: %s\n" +
                            "Farmland Size: %.2f decares\n" +
                            "Total Cost: %.2f VND\n\n" +
                            "We will keep you updated on the progress, and once the service is complete, we'll provide you with the necessary details. If you have any questions, feel free to contact us.\n\n"
                            +
                            "Thank you for your trust in HoverSprite Service.\n\n" +
                            "Best regards,\n" +
                            "HoverSprite Team",
                    farmerDTO.getFullName(),
                    booking.getId(),
                    gregorianDate,
                    lunarDateString,
                    timeSlotDTO.getStartTime(),
                    farmerDTO.getAddress(),
                    booking.getFarmlandArea(),
                    booking.getTotalCost());
        }

        return String.format(
                "Dear %s,\n\n" +
                        "We are delighted to inform you that your booking with HoverSprite Service has been successfully completed.\n\n"
                        +
                        "Booking Details:\n" +
                        "Booking ID: %d\n" +
                        "Date (Gregorian): %s\n" +
                        "Date (Lunar): %s\n" +
                        "Time Slot: %s\n" +
                        "Location: %s\n" +
                        "Farmland Size: %.2f decares\n" +
                        "Total Cost: %.2f VND\n\n" +
                        "We hope our service met your expectations. Should you require any further assistance or have feedback, please don't hesitate to contact us.\n\n"
                        +
                        "Thank you for choosing HoverSprite Service. We look forward to serving you again in the future.\n\n"
                        +
                        "Best regards,\n" +
                        "HoverSprite Team",
                farmerDTO.getFullName(),
                booking.getId(),
                gregorianDate,
                lunarDateString,
                timeSlotDTO.getStartTime(),
                farmerDTO.getAddress(),
                booking.getFarmlandArea(),
                booking.getTotalCost());
    }

}