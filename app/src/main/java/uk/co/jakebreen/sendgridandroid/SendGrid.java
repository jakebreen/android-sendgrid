package uk.co.jakebreen.sendgridandroid;

public class SendGrid {

    private static final String MAIL_URL = "mail/send";

    private String SENDGRID_API_KEY = "***REMOVED***";
    private String credentials;
    private SendGridCall api;


    private SendGrid(String apiKey) {
        this.credentials = createCredentials(apiKey);
        api = new SendGridCall();
    }

    public SendGrid create(String apiKey) {
        return new SendGrid(apiKey);
    }

    public SendGridResponse send() throws Exception {
        return api.call(MAIL_URL, credentials).call();
    }

    private String createCredentials(String key) {
        return String.format("Bearer %s", key);
    }
}
