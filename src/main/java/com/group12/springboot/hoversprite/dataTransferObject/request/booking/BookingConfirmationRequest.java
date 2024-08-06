package com.group12.springboot.hoversprite.dataTransferObject.request.booking;

public class BookingConfirmationRequest {
    private Long id;

    private String receptionist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceptionist() {
        return receptionist;
    }

    public void setReceptionist(String receptionist) {
        this.receptionist = receptionist;
    }
}