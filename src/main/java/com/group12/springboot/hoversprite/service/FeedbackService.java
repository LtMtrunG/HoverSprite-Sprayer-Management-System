package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.request.feedback.FeedbackCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.FeedbackCreationResponse;
import com.group12.springboot.hoversprite.entity.Feedback;
import com.group12.springboot.hoversprite.entity.enums.BookingStatus;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @PreAuthorize("hasRole('FARMER')")
    public FeedbackCreationResponse createFeedBack(FeedbackCreationRequest request){
        if(request.getBooking().getStatus() != BookingStatus.COMPLETED){
            throw new CustomException(ErrorCode.INVALID_ACTION);
        }

        Feedback feedback = new Feedback();
        feedback.setDescription(request.getDescription());
        feedback.setRating(request.getRating());
        feedback.setBooking(request.getBooking());
        feedback.setImages(request.getImages());

        feedbackRepository.save(feedback);
        return new FeedbackCreationResponse(feedback);
    }
}
