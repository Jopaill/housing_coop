package com.jordanpaille.housing.coop.services.email.zohoemails;

import com.jordanpaille.housing.coop.services.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static com.jordanpaille.housing.coop.services.passwordreset.PasswordResetServiceImpl.LIMITE_DE_COURRIELS_POUR_31_JOURS;


@Slf4j
//@Service
public class EmailServiceImplZoho implements EmailService {

    private final RestClient restClient;
    private final boolean skipEmails;
    private final String fromEmail;
    private final String subject;
    private final String baseUrl;

    public EmailServiceImplZoho(
            @Value("${custom.email.from}") String fromEmail,
            @Value("${custom.email.subject}") String subject,
            @Value("${custom.email.skip:false}") boolean skipEmails,
            @Value("${custom.email.zoho.api.key}") String zohoApiKey,
            @Value("${base.url}") String baseUrl
    ) {
        if (null == zohoApiKey) {
            throw new NullPointerException("zohoApiKey is null. Please `set CUSTOM_EMAIL_ZOHO_API_KEY=Md...=`");
        }
        this.subject = subject;
        this.fromEmail = fromEmail;
        this.baseUrl = baseUrl;
        this.skipEmails = skipEmails;
        this.restClient = RestClient.builder()
                .baseUrl("https://cpaas.zohocloud.ca")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Zoho-enczapikey " + zohoApiKey
                )
                .build();
    }

    public boolean sendResetEmailSuccess(String to, String rawToken, long countOfEmailsSentForThisMember) {
        log.info("Sending reset email to {} with rawToken: {}", to, rawToken);

        if (this.skipEmails) {
            log.warn("Skipping the delivery of the email");
            return true;
        }
        try {
            ZohoEmailRequest request = ZohoEmailRequest.builder()
                    .fromEmail(this.fromEmail)
                    .fromName("Coop JM")
                    .toEmail(to)
                    .bccEmail("admincoopjm@gmail.com")
                    .subject(this.subject)
                    .htmlBody(getHtmlBody(rawToken, countOfEmailsSentForThisMember))
                    .build();

            sendEmail(request);
            return true;
        } catch (RestClientException e) {
            log.error("Failed to send reset email.", e);
            return false;
        }
    }

    private String getHtmlBody(String rawToken, long countOfEmailsSentForThisMember) {
        String resetLink = this.baseUrl + "/public/reset-password?token=" + rawToken;

        return "<p>Salut,<br/>" +
                "<br/>" +
                "Veuillez créer votre nouveau mot de passe: <a href=\"" + resetLink + "\">" + resetLink + "</a><br/>" +
                "<br/>" +
                "Vous avez une limite de " + LIMITE_DE_COURRIELS_POUR_31_JOURS + " courriels réinitialisation de mot de passe pour les derniers 31 jours.<br/>" +
                "Durant les 31 derniers jours, vous avez reçu <strong>" + (countOfEmailsSentForThisMember + 1) + "</strong> courriels de réinitialisation." +
                "<br/>" +
                "<br/>" +
                "Ne pas répondre à ce courriel<br/>" +
                "<br/>" +
                "Merci,<br>" +
                "Site Web de la Coop Jeanne-Mance</p>";
    }

    private void sendEmail(ZohoEmailRequest request) throws RestClientException {
        try {
            String response = restClient.post()
                    .uri("/v1.1/email")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.info("Zoho email sent successfully: {}", response);

        } catch (RestClientException e) {
            log.error("Failed to send Zoho email: {}", e.getMessage(), e);
            throw e;
        }
    }

}
