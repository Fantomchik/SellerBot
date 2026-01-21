package ru.sellerbot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "price_request")
public class PriceRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String modelKey;

    @Column(nullable = false)
    private String modelDisplay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceRequestStatus status;

    @Column(nullable = false)
    private Long managerChatId;

    @Column
    private Integer managerRequestMessageId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime resolvedAt;

    @Column
    private Integer resolvedPrice;
}
