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

    /**
     * Returns a SendGrid instance tied to your API key that is used
     * for initiating mail send requests.
     *
     * @param apiKey your SendGrid API key
     * @return the SendGrid instance tied to your API key
     */
    public static SendGrid create(@NonNull String apiKey) {
        return new SendGrid(apiKey);
    }

    /**
     * API mail send request, provide with a {@link SendGridMail} and returns a Callable
     * {@link SendGridResponse}.
     *
     * The response will contain a successful {@link SendGridResponse#isSuccessful()}
     * state along with any associated error {@link SendGridResponse#getErrorMessage()}
     * in the event on an unsuccessful response.
     *
     * @param mail the SendGridMail to send to the API
     * @return the response generated from the API request
     */
    public Callable<SendGridResponse> send(@NonNull SendGridMail mail) {
        return api.call(MAIL_URL, credentials, SendGridMailBody.create(mail));
    }

    private String createCredentials(String key) {
        return String.format("Bearer %s", key);
    }
}
