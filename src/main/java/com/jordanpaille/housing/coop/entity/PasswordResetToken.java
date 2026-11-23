package com.jordanpaille.housing.coop.entity;

import java.time.Instant;

public record PasswordResetToken (
        Long id,
        String tokenHash,
        Instant expiresAt,
        boolean used,
        String userId
) {
}
