package com.group12.springboot.hoversprite.feedback.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "TBL_FEEDBACKS")
public class Feedback {
    // Getters and Setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @Min(1)
    @Max(5)
    @Column(name = "overall_rating")
    private int overalRating;

    @Min(1)
    @Max(5)
    @Column(name = "attentive_rating")
    private int attentiveRating;

    @Min(1)
    @Max(5)
    @Column(name = "friendly_rating")
    private int friendlyRating;

    @Min(1)
    @Max(5)
    @Column(name = "professional_rating")
    private int professionalRating;

    @Column(name = "images")
    private String[] images;
}