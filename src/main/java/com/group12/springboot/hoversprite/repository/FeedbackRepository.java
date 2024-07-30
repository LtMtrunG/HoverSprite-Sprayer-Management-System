package com.group12.springboot.hoversprite.repository;

import com.group12.springboot.hoversprite.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
