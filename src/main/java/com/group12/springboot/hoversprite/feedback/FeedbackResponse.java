package com.group12.springboot.hoversprite.feedback;

import com.group12.springboot.hoversprite.feedback.entity.Feedback;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FeedbackResponse {
    private Long id;
    private String description;
    private int overall_rating;
    private int attentive_rating;
    private int friendly_rating;
    private int professional_rating;
    private String[] images;

    public FeedbackResponse(Feedback feedback) {
        this.id = feedback.getId();
        this.description = feedback.getDescription();
        this.overall_rating = feedback.getOveralRating();
        this.attentive_rating = feedback.getAttentiveRating();
        this.friendly_rating = feedback.getFriendlyRating();
        this.professional_rating = feedback.getProfessionalRating();
        this.images = feedback.getImages();
    }
}
