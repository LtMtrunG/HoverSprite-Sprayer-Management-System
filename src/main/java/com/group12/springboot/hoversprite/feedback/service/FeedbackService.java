package com.group12.springboot.hoversprite.feedback.service;

import com.group12.springboot.hoversprite.booking.BookingAPI;
import com.group12.springboot.hoversprite.booking.BookingDTO;
import com.group12.springboot.hoversprite.config.CustomUserDetails;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.feedback.FeedbackCreationRequest;
import com.group12.springboot.hoversprite.feedback.FeedbackCreationResponse;
import com.group12.springboot.hoversprite.feedback.repository.FeedbackRepository;
import com.group12.springboot.hoversprite.feedback.FeedbackResponse;
import com.group12.springboot.hoversprite.feedback.entity.Feedback;
import com.group12.springboot.hoversprite.user.UserAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private BookingAPI bookingAPI;

    @PreAuthorize("hasRole('FARMER')")
    public FeedbackCreationResponse createFeedBack(FeedbackCreationRequest request){

        BookingDTO bookingDTO = bookingAPI.findBookingById(request.getBookingId());
        if (bookingDTO == null) {
            throw new CustomException(ErrorCode.BOOKING_NOT_EXISTS);
        }

        if (!bookingDTO.getStatus().toString().equals("COMPLETED")) {
            throw new CustomException(ErrorCode.BOOKING_NOT_COMPLETE);
        }

        if (bookingAPI.doesBookingHaveFeedback(request.getBookingId())) {
            throw new CustomException(ErrorCode.FEEDBACK_GIVEN);
        }

        Long farmerId = getCurrentUserId();
        if (!bookingAPI.doesFarmerOwnBooking(request.getBookingId(), farmerId)) {
            throw new CustomException(ErrorCode.FARMER_NOT_OWNED);
        }

        if (request.getImages().size() > 5) {
            throw new CustomException(ErrorCode.IMAGES_EXCEED);
        }

        System.out.println(request.getImages());

        Feedback feedback = new Feedback();
        feedback.setDescription(request.getDescription());
        feedback.setOveralRating(request.getOverall_rating());
        feedback.setAttentiveRating(request.getAttentive_rating());
        feedback.setFriendlyRating(request.getFriendly_rating());
        feedback.setProfessionalRating(request.getProfessional_rating());
        // Ensure that the images list is correctly assigned
        List<String> images = request.getImages();
        if (images != null && !images.isEmpty()) {
            feedback.setImages(new ArrayList<>(images));  // Ensure you're creating a new list to avoid overwrites
        } else {
            System.out.println("No images found in the request.");
        }

        Feedback savedFeedback = feedbackRepository.save(feedback);

        bookingAPI.saveFeedbackToBooking(request.getBookingId(), savedFeedback.getId());

        return new FeedbackCreationResponse(feedback);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                // Assuming CustomUserDetails holds the User ID
                return ((CustomUserDetails) principal).getId();
            }
        }

        throw new CustomException(ErrorCode.USER_NOT_EXISTS);
    }

    public FeedbackResponse getFeedbackById(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_EXISTS));
        if (!bookingAPI.hasPermissionOrNot(feedbackId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return new FeedbackResponse(feedback);
    }
}
