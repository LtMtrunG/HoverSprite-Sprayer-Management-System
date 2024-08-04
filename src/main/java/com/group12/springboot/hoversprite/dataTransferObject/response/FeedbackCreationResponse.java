package com.group12.springboot.hoversprite.dataTransferObject.response;

import com.group12.springboot.hoversprite.entity.Feedback;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class FeedbackCreationResponse {
    private Long id;
    private String description;
    private int rating;
    private String[] images;

    public FeedbackCreationResponse(Feedback feedback){
        this.id = feedback.getId();
        this.description = feedback.getDescription();
        this.rating = feedback.getRating();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}
