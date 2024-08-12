package com.group12.springboot.hoversprite.booking;

public class BookingConfirmationRequest {
    private Long id;

    private Long receptionistId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceptionistId() {
        return receptionistId;
    }

    public void setReceptionistId(Long receptionistId) {
        this.receptionistId = receptionistId;
    }
}