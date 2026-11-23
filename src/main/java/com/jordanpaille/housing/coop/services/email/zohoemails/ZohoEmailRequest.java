package com.jordanpaille.housing.coop.services.email.zohoemails;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public record ZohoEmailRequest(
        @JsonProperty("from") EmailAddress from,
        @JsonProperty("to") List<Recipient> to,
        @JsonProperty("cc") List<Recipient> cc,
        @JsonProperty("bcc") List<Recipient> bcc,
        @JsonProperty("subject") String subject,
        @JsonProperty("htmlbody") String htmlBody
) {

    public record EmailAddress(
            @JsonProperty("address") String address,
            @JsonProperty("name") String name
    ) {
    }

    public record Recipient(
            @JsonProperty("email_address") EmailAddress emailAddress
    ) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String fromEmail;
        private String fromName;
        private String toEmail;
        private String bccEmail;
        private String subject;
        private String htmlBody;

        private Builder() {
        }

        public Builder fromEmail(String fromEmail) {
            this.fromEmail = fromEmail;
            return this;
        }

        public Builder fromName(String fromName) {
            this.fromName = fromName;
            return this;
        }

        public Builder toEmail(String toEmail) {
            this.toEmail = toEmail;
            return this;
        }

        public Builder bccEmail(String bccEmail) {
            this.bccEmail = bccEmail;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder htmlBody(String htmlBody) {
            this.htmlBody = htmlBody;
            return this;
        }

        public ZohoEmailRequest build() {
            requireNonNull(fromEmail, "fromEmail");
            requireNonNull(fromName, "fromName");
            requireNonNull(toEmail, "toEmail");
            requireNonNull(bccEmail, "bccEmail");
            requireNonNull(subject, "subject");
            requireNonNull(htmlBody, "htmlBody");

            return new ZohoEmailRequest(
                    new EmailAddress(fromEmail, fromName),
                    List.of(new Recipient(new EmailAddress(toEmail, null))),
                    null,
                    List.of(new Recipient(new EmailAddress(bccEmail, null))),
                    subject,
                    htmlBody
            );
        }

        private static void requireNonNull(String value, String fieldName) {
            Objects.requireNonNull(value, fieldName + " must not be null");
        }
    }
}