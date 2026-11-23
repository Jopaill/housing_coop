package com.jordanpaille.housing.coop.services.passwordreset;

public interface PasswordResetService {

    void initiatePasswordReset(String emailAddress);

    boolean resetPassword(String rawToken, String newPassword);

}
