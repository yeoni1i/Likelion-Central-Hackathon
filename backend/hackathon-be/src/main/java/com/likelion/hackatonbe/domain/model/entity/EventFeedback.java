package com.likelion.hackatonbe.domain.model.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "event_feedback", uniqueConstraints = @UniqueConstraint(
        name = "uk_feedback_user_event", columnNames = {"user_id", "client_event_id"}))
public class EventFeedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "client_event_id", nullable = false, length = 64) private String clientEventId;
    @Column(nullable = false, length = 30) private String label;
    @Column(length = 200) private String context;
    @Column(name = "raw_window_included", nullable = false) private boolean rawWindowIncluded;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected EventFeedback() {}
    public EventFeedback(Long userId, String clientEventId, String label, String context, boolean raw, Instant now) {
        this.userId=userId; this.clientEventId=clientEventId; this.label=label;
        this.context=context; this.rawWindowIncluded=raw; this.createdAt=now;
    }
}
