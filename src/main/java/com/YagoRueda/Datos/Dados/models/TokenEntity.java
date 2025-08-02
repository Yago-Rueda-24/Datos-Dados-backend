package com.YagoRueda.Datos.Dados.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String tokenId;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Getter
    @Setter
    @Column(nullable = false)
    private Instant createdAt;

    @Getter
    @Setter
    @Column(nullable = false)
    private Instant expiresAt;

    @Getter
    @Setter
    private boolean expired;

    @Getter
    @Setter
    private boolean revoked;
}
