package com.group12.springboot.hoversprite.feedback.repository;

import com.group12.springboot.hoversprite.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findById(Long id);
}
