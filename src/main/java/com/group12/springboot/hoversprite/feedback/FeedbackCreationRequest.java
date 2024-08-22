package com.group12.springboot.hoversprite.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FeedbackCreationRequest {
    private String description;
    @Min(1)
    @Max(5)
    private int overall_rating;
    @Min(1)
    @Max(5)
    private int attentive_rating;
    @Min(1)
    @Max(5)
    private int friendly_rating;
    @Min(1)
    @Max(5)
    private int professional_rating;
    private String[] images;
    private Long bookingId;
}