package com.tomcvt.brickshop.security;

import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID token;
    private Long userId;

    public VerificationToken() {
    }
    public VerificationToken(UUID token, Long userId) {
        this.token = token;
        this.userId = userId;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public UUID getToken() {
        return token;
    }
    public void setToken(UUID token) {
        this.token = token;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
