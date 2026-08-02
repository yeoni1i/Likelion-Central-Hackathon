package com.likelion.hackatonbe.domain.model.repository;
import com.likelion.hackatonbe.domain.model.entity.EventFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EventFeedbackRepository extends JpaRepository<EventFeedback, Long> {}
