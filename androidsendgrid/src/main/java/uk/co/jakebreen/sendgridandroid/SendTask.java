package uk.co.jakebreen.sendgridandroid;

public class SendTask {

    private final SendGrid sendGrid;

    public SendTask(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    /**
     * API mail send request, provide with a {@link SendGridMail} and returns a {@link SendGridResponse}.
     * Rethrows any exceptions.
     *
     * @param mail the SendGridMail to send to the API
     * @return the response generated from the API request
     */
    public SendGridResponse send(SendGridMail mail) throws Exception {
        final SendGridResponse response = new SendTaskAsync(sendGrid, mail).execute().get();
        if (response.getException() != null)
            throw response.getException();
        return response;
    }

}
