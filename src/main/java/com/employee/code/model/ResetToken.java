package com.employee.code.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="otpinfo")
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(nullable = false,unique = true)
    private String token ;
    @Column(nullable = false)
    private String email ;
    @Column(nullable = false)
    private LocalDateTime createdAt ;
    @Column(nullable = false)
    private LocalDateTime expiredAt ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Override
    public String toString() {
        return "ResetToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
