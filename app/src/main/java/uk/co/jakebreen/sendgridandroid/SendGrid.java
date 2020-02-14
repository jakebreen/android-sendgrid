package uk.co.jakebreen.sendgridandroid;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;

public class SendGrid {

    private static final String MAIL_URL = "mail/send";

    private String credentials;
    private SendGridCall api;

    private SendGrid(String apiKey) {
        this.credentials = createCredentials(apiKey);
        api = new SendGridCall();
    }

    public static SendGrid create(@NonNull String apiKey) {
        return new SendGrid(apiKey);
    }

    public Callable<SendGridResponse> send(@NonNull SendGridMail mail) {
        return api.call(MAIL_URL, credentials, SendGridMailBody.create(mail));
    }

    private String createCredentials(String key) {
        return String.format("Bearer %s", key);
    }
}
