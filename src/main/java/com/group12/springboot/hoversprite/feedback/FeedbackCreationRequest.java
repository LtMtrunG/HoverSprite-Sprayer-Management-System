package com.group12.springboot.hoversprite.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> images;
    private Long bookingId;
}