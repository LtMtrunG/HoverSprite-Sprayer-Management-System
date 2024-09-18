package com.group12.springboot.hoversprite.qr.controller;

import com.group12.springboot.hoversprite.common.ApiResponse;
import com.group12.springboot.hoversprite.qr.service.QRService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qr")
public class QRController {

    @Autowired
    private QRService qrService;

    @GetMapping(value = "/generateQRCode", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQRCode(@RequestParam("bookingId") String text, HttpServletResponse response) throws Exception {
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        return qrService.generateQRCode(text, 300, 300); // Adjust size as needed
    }
}