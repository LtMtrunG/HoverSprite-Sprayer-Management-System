package com.group12.springboot.hoversprite.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "TBL_INVALIDATED_TOKENS")
public class InvalidatedToken {
    @Id
    private String id;

    @Column(name="expiry_time")
    private Date expiryTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }
}
