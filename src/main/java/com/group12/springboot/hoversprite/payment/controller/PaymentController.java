package com.group12.springboot.hoversprite.payment.controller;

import com.group12.springboot.hoversprite.common.ApiResponse;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.payment.service.PaymentService;
import com.group12.springboot.hoversprite.user.UserResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-payment-intent")
    public ApiResponse<Map<String, String>> createPaymentIntent(@RequestParam("amount") Long amount) {
        Stripe.apiKey = "sk_test_51PseeZH7IZ6oR42y5ngsfNNe8AlvazIVTvR7O7alqwMdelAjhw5vPIRtDNlxcRR5yXM7waayBch6r5KON6gspImE00NWHndU6y";

        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();

        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amount)  // in cents
                            .setCurrency("usd")
                            .addPaymentMethodType("card")// Currency
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("clientSecret", paymentIntent.getClientSecret());

            apiResponse.setResult(responseData);
            return apiResponse;
        } catch (StripeException e) {
            throw new CustomException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @PostMapping("/card")
    public ApiResponse<String> payByCard(@RequestParam("id") Long bookingId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult(paymentService.payByCard(bookingId));
        return apiResponse;
    }

    @PostMapping("/cash")
    public ApiResponse<String> payByCash(@RequestParam("id") Long bookingId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult(paymentService.payByCash(bookingId));
        return apiResponse;
    }
}
