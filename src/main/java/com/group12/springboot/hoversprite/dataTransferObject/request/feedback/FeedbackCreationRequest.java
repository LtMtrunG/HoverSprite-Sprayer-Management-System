package com.group12.springboot.hoversprite.dataTransferObject.request.feedback;

import com.group12.springboot.hoversprite.entity.Booking;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class FeedbackCreationRequest {
    private String description;
    @Min(0)
    @Max(5)
    private int rating;
    private String[] images;
    private Booking booking;

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

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
