package com.jordanpaille.housing.coop.dao.passwordreset;

import com.jordanpaille.housing.coop.entity.PasswordResetToken;

import java.time.Instant;

public interface PasswordResetTokenDao {

    void save(PasswordResetToken token);

    PasswordResetToken findByTokenHash(String tokenHash);

    boolean lockUserForReset(String userId);

    long countCreatedSince(String userId, Instant since);

    void markUsed(Long id);
}
