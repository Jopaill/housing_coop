package com.jordanpaille.housing.coop.services.Email;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import com.postmarkapp.postmark.client.exception.PostmarkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class EmailServiceImplPostmark implements EmailService {

    private final String postmarkMessageStream;
    private final String fromEmail;
    private final String subject;
    private final String baseUrl;
    private final ApiClient client;


    public EmailServiceImplPostmark(
            @Value("${POSTMARK_API_TOKEN}") String api_token,
            @Value("${POSTMARK_FROM_EMAIL}") String fromEmail,
            @Value("${POSTMARK_SUBJECT}") String subject,
            @Value("${POSTMARK_MESSAGE_STREAM}") String postmarkMessageStream,
            @Value("${base.url}") String baseUrl) {
        
        log.info("Constructing EmailServiceImplMailgun with fromEmail: {}, subject: {}, postmarkMessageStream: {}, baseUrl: {}", fromEmail, subject, postmarkMessageStream, baseUrl);
        log.debug("POSTMARK_API_TOKEN: {}", api_token);
        this.postmarkMessageStream = postmarkMessageStream;
        this.fromEmail = fromEmail;
        this.subject = subject;
        this.baseUrl = baseUrl;
        this.client = Postmark.getApiClient(api_token);
        log.info("EmailServiceImplMailgun constructed");
    }

    public boolean sendResetEmailSuccess(String emailAddress, String rawToken) {
        log.info("Sending reset email to {} with rawToken: {}", emailAddress, rawToken);

        String resetLink = baseUrl + "/reset-password?token=" + rawToken;
        
        Message message = new Message(fromEmail, emailAddress, subject, "<p>Click <a href=\"" + resetLink + "\">here</a> to reset your password. Link expires in 1 hour.</p>");
        message.setMessageStream(postmarkMessageStream);
        try {
            MessageResponse response = client.deliverMessage(message);
            log.info("Email sent successfully: {}", response);
            return true;
        } catch (PostmarkException | IOException e) {
            log.error("Failed to send reset email.", e);
            return false;
        }
    }
}
