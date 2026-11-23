package com.jordanpaille.housing.coop.services.passwordreset;

import com.jordanpaille.housing.coop.dao.passwordreset.PasswordResetTokenDao;
import com.jordanpaille.housing.coop.entity.PasswordResetToken;
import com.jordanpaille.housing.coop.services.email.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    public static final int LIMITE_DE_COURRIELS_POUR_31_JOURS = 20;

    private final PasswordResetTokenDao tokenDao;
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // --------------------- START: Reset Password Functionality ----------------------------------------
    /** Step 1: Generate token & send reset email */
    @Transactional
    public void initiatePasswordReset(String emailAddress) {
        if (!userDetailsManager.userExists(emailAddress)) {
            log.warn("no account found for {}", emailAddress);
            return;
        }

        if (!tokenDao.lockUserForReset(emailAddress)) {
            return;
        }

        Instant now = Instant.now();
        long countOfEmailsSentForThisMember = tokenDao.countCreatedSince(emailAddress, now.minus(Duration.ofDays(31)));
        if (countOfEmailsSentForThisMember >= LIMITE_DE_COURRIELS_POUR_31_JOURS) {
            log.warn("password reset email limit reached for {}", emailAddress);
            return;
        }

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = sha256(rawToken);

        if (!emailService.sendResetEmailSuccess(emailAddress, rawToken, countOfEmailsSentForThisMember)) {
            return; // ToDo: Alert the user that the service failed and to contact support
        }

        PasswordResetToken token = new PasswordResetToken(
                null,
                tokenHash,
                Instant.now().plusSeconds(3600),
                false,
                emailAddress);
        tokenDao.save(token);
    }

    /** Step 2: Validate token and update password */
    public boolean resetPassword(String rawToken, String newPassword) {
        String tokenHash = sha256(rawToken);
        PasswordResetToken token = tokenDao.findByTokenHash(tokenHash);

        if (null == token || token.used() || token.expiresAt().isBefore(Instant.now())) {
            return false;
        }

        UserDetails existing = userDetailsManager.loadUserByUsername(token.userId());
        UserDetails updated = User
                .withUserDetails(existing)
                .password(passwordEncoder.encode(newPassword))
                .disabled(false)
                .build();

        userDetailsManager.updateUser(updated);
        tokenDao.markUsed(token.id());
        return true;
    }


    // --------------------- END: Reset Password Functionality ----------------------------------------

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getUrlEncoder().encodeToString(digest.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
