package com.group12.springboot.hoversprite.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.group12.springboot.hoversprite.booking.BookingAPI;
import com.group12.springboot.hoversprite.booking.BookingDTO;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QRService {

    private final BookingAPI bookingAPI;

    public byte[] generateQRCode(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        Long bookingId = Long.parseLong(text);

        BookingDTO bookingDTO = bookingAPI.findBookingById(bookingId);
        if (bookingDTO == null) {
            throw new CustomException(ErrorCode.BOOKING_NOT_EXISTS);
        }

        String url = String.format("http://localhost:5500/hoversprite/BookingManagement/booking-management?bookingId=%s&farmerId=%s", bookingId, bookingDTO.getFarmerId());
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        System.out.println(pngOutputStream.toByteArray());
        return pngOutputStream.toByteArray();
    }
}
