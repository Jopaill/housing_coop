package com.jordanpaille.housing.coop.services.Email;

public interface EmailService {
    boolean sendResetEmailSuccess(String emailAddress, String rawToken);
}
