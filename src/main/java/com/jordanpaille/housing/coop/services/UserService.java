package com.jordanpaille.housing.coop.services;

import com.jordanpaille.housing.coop.dao.PasswordResetTokenDao;
import com.jordanpaille.housing.coop.dao.UserDao;
import com.jordanpaille.housing.coop.entity.PasswordResetToken;
import com.jordanpaille.housing.coop.services.Email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordResetTokenDao tokenDao;
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserDao userDao,
                       PasswordResetTokenDao tokenDao,
                       UserDetailsManager userDetailsManager,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userDao = userDao;
        this.tokenDao = tokenDao;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    // ------- Reset Password Functionality ------------
    /** Step 1: Generate token & send reset email */
    public void initiatePasswordReset(String emailAddress) {
        if (!userDetailsManager.userExists(emailAddress)) {
            log.warn("no account found for {}", emailAddress);
            return;
        }

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = sha256(rawToken);

        if (!emailService.sendResetEmailSuccess(emailAddress, rawToken)) {
            return; // ToDo: Alert the user that the service failed and to contact support
        }

        PasswordResetToken token = new PasswordResetToken();
        token.setTokenHash(tokenHash);
        token.setUserId(emailAddress);
        token.setExpiresAt(Instant.now().plusSeconds(3600)); // 1 hour
        token.setUsed(false);
        tokenDao.save(token);
    }

    /** Step 2: Validate token and update password */
    public boolean resetPassword(String rawToken, String newPassword) {
        String tokenHash = sha256(rawToken);
        PasswordResetToken token = tokenDao.findByTokenHash(tokenHash);

        if (null == token || token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            return false;
        }

        UserDetails existing = userDetailsManager.loadUserByUsername(token.getUserId());
        UserDetails updated = User
                .withUserDetails(existing)
                .password(passwordEncoder.encode(newPassword))
                .disabled(false)
                .build();

        userDetailsManager.updateUser(updated);
        tokenDao.markUsed(token.getId());
        return true;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getUrlEncoder().encodeToString(digest.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
