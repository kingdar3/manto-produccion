package com.guardianapp.infrastructure.adapter.out.persistence.entity;

import com.guardianapp.domain.enums.SmsThreatAlertStatus;
import com.guardianapp.domain.enums.UrlThreatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for SMS threat alerts.
 */
@Entity
@Table(name = "sms_threat_alerts", indexes = {
    @Index(name = "idx_sms_alert_link_id", columnList = "link_id"),
    @Index(name = "idx_sms_alert_host_id", columnList = "host_user_id"),
    @Index(name = "idx_sms_alert_status", columnList = "status"),
    @Index(name = "idx_sms_alert_protected_user", columnList = "protected_user_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsThreatAlertEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "link_id", nullable = false)
    private UUID linkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_id", insertable = false, updatable = false)
    private LinkEntity link;

    @Column(name = "protected_user_id", nullable = false)
    private UUID protectedUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protected_user_id", insertable = false, updatable = false)
    private UserEntity protectedUser;

    @Column(name = "host_user_id", nullable = false)
    private UUID hostUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id", insertable = false, updatable = false)
    private UserEntity hostUser;

    @Column(name = "sender", nullable = false, length = 160)
    private String sender;

    @Column(name = "message_excerpt", nullable = false, length = 5000)
    private String messageExcerpt;

    @Column(name = "detected_url", nullable = false, length = 2048)
    private String detectedUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_status", nullable = false, length = 20)
    private UrlThreatStatus analysisStatus;

    @Column(name = "analysis_reason", length = 1000)
    private String analysisReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SmsThreatAlertStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by_user_id")
    private UUID resolvedByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_user_id", insertable = false, updatable = false)
    private UserEntity resolvedByUser;

    @Column(name = "resolution_note", length = 500)
    private String resolutionNote;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
