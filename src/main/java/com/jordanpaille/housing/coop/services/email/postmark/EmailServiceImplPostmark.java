package com.jordanpaille.housing.coop.services.email.postmark;

import com.jordanpaille.housing.coop.services.email.EmailService;
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
//@Service
public class EmailServiceImplPostmark implements EmailService {

    private final String postmarkMessageStream;
    private final String fromEmail;
    private final String subject;
    private final String baseUrl;
    private final ApiClient client;
    private final boolean skipEmails;


    public EmailServiceImplPostmark(
            @Value("${postmark.api.token}") String api_token,
            @Value("${custom.email.from}") String fromEmail,
            @Value("${custom.email.subject}") String subject,
            @Value("${postmark.message.stream}") String postmarkMessageStream,
            @Value("${base.url}") String baseUrl,
            @Value("${custom.email.skip:false}") boolean skipEmails) {
        
        log.info("Constructing EmailServiceImplMailgun with fromEmail: {}, subject: {}, postmarkMessageStream: {}, baseUrl: {}", fromEmail, subject, postmarkMessageStream, baseUrl);
        log.debug("postmark.api.token: {}", api_token);
        this.postmarkMessageStream = postmarkMessageStream;
        this.fromEmail = fromEmail;
        this.subject = subject;
        this.baseUrl = baseUrl;
        this.client = Postmark.getApiClient(api_token);
        this.skipEmails = skipEmails;
        log.info("EmailServiceImplMailgun constructed with skipEmails = {}", skipEmails);
    }

    public boolean sendResetEmailSuccess(String emailAddress, String rawToken, long countOfEmailsSentForThisMember) {
        log.info("Sending reset email to {} with rawToken: {}", emailAddress, rawToken);

        if (skipEmails) {
            log.warn("Skipping the delivery of the email");
            return true;
        }

        Message message = getMessage(emailAddress, rawToken, countOfEmailsSentForThisMember);
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

    private Message getMessage(String emailAddress, String rawToken, long countOfEmailsSentForThisMember) {
        String resetLink = baseUrl + "/public/reset-password?token=" + rawToken;

        String htmlEmailBody = "<p>Bonjour,<br/><br/>Créer un nouveau mot de passe:<br/>" +
                "<a href=\"" + resetLink + "\">" + resetLink + "</a><br/>" +
                "<br/><br/>" +
                "<strong>Important</strong>: Vous avez une limite de 2 courriels réinitialisation de mot de passe pour les derniers 31 jours.<br/>" +
                "Durant les 31 derniers jours, vous avez reçu <strong>" + (countOfEmailsSentForThisMember + 1) + "</strong> courriels de réinitialisation." +
                "<br/><br/>" +
                "Veuillez ne pas répondre à ce courriel<br/><br/>" +
                "Merci,<br>" +
                "Site Web de la Coop Jeanne-Mance</p>";

        return new Message(fromEmail, emailAddress, subject, htmlEmailBody);
    }
}
