package com.jordanpaille.housing.coop;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;

public class TestingSendingMail {
    static void main() {
        System.out.println("Testing sending mail functionality");
        //

        Email email = new Email();

        email.setFrom("Jordan", "jordan@coopjm.ca");
        email.addRecipient("JoPaille", "jordan1592@gmail.com");
        email.setSubject("Hello World");

        email.setHtml("<p>Salut TiffLe,<br><br>This is a test email<br><br>Merci,<br>Jordan</p>");

        MailerSend ms = new MailerSend();

        ms.setToken(""); // ToDo: Token has been put in a file that's gitignore. To improve the secrets initialization

        try {
            MailerSendResponse response = ms.emails().send(email);
            System.out.println(response.messageId);
        } catch (MailerSendException e) {
            e.printStackTrace();
        }
    }
}
