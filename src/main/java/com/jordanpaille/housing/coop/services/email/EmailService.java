package com.jordanpaille.housing.coop.services.email;

public interface EmailService {
    boolean sendResetEmailSuccess(String emailAddress, String rawToken, long countOfEmailsSentForThisMember);
}
