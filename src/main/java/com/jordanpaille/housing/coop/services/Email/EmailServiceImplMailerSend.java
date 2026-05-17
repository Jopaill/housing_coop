package com.jordanpaille.housing.coop.services.Email;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service // Commenting the @Service annotation to disable automatic bean registration
public class EmailServiceImplMailerSend implements EmailService {

    private final MailerSend mailerSend = new MailerSend();
    private final String baseUrl;

    public EmailServiceImplMailerSend(
            @Value("${MAILERSEND_API_TOKEN}") String mailersendToken,
            @Value("${base-url}") String baseUrl) {
        log.info("Initializing EmailService with baseUrl: {}", baseUrl);
        mailerSend.setToken(mailersendToken);
        this.baseUrl = baseUrl;
    }


    public boolean sendResetEmailSuccess(String emailAddress, String rawToken) {
        log.info("Sending reset email to {} with rawToken: {}", emailAddress, rawToken);
        String resetLink = baseUrl + "/reset-password?token=" + rawToken;

        Email email = new Email();
        email.setFrom("Coop J-M", "coopjm@coopjm.ca");
        email.addRecipient("name", emailAddress); // ToDo: Fix this!
        email.setSubject("Reset your password");
        email.setHtml("<p>Click <a href=\"" + resetLink + "\">here</a> to reset your password. Link expires in 1 hour.</p>");

        try {
            mailerSend.emails().send(email);
            log.info("Reset email sent to {}", emailAddress);
            return true;
        } catch (MailerSendException e) {
            log.error("Failed to send reset email to {}", emailAddress, e);
            return false;
        }
    }
}
