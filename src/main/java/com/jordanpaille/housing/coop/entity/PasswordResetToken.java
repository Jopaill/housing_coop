package com.jordanpaille.housing.coop.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PasswordResetToken {
    private Long id;
    private String tokenHash;
    private Instant expiresAt;
    private boolean used;
    private String userId;
}
