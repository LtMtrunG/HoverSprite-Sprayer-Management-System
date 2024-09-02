package com.group12.springboot.hoversprite.feedback.service;

import com.group12.springboot.hoversprite.booking.BookingAPI;
import com.group12.springboot.hoversprite.config.CustomUserDetails;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.feedback.FeedbackCreationRequest;
import com.group12.springboot.hoversprite.feedback.FeedbackCreationResponse;
import com.group12.springboot.hoversprite.feedback.repository.FeedbackRepository;
import com.group12.springboot.hoversprite.feedback.FeedbackResponse;
import com.group12.springboot.hoversprite.feedback.entity.Feedback;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

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
        if (!bookingAPI.isBookingExist(request.getBookingId())) {
            throw new CustomException(ErrorCode.BOOKING_NOT_EXISTS);
        }

        if (bookingAPI.doesBookingHaveFeedback(request.getBookingId())) {
            throw new CustomException(ErrorCode.FEEDBACK_GIVEN);
        }

        Long farmerId = getCurrentUserId();
        if (!bookingAPI.doesFarmerOwnBooking(request.getBookingId(), farmerId)) {
            throw new CustomException(ErrorCode.FARMER_NOT_OWNED);
        }

        if (request.getImages().length > 5) {
            throw new CustomException(ErrorCode.IMAGES_EXCEED);
        }

        Feedback feedback = new Feedback();
        feedback.setDescription(request.getDescription());
        feedback.setOveralRating(request.getOverall_rating());
        feedback.setAttentiveRating(request.getAttentive_rating());
        feedback.setFriendlyRating(request.getFriendly_rating());
        feedback.setProfessionalRating(request.getProfessional_rating());
        feedback.setImages(request.getImages());

        System.out.println(Arrays.toString(feedback.getImages()));

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
