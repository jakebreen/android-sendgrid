package uk.co.jakebreen.sendgridandroid;

import android.os.AsyncTask;

public class SendTask extends AsyncTask<Void, Void, SendGridResponse> {

    private final SendGrid sendGrid;
    private final SendGridMail mail;

    public SendTask(SendGrid sendGrid, SendGridMail mail) {
        this.sendGrid = sendGrid;
        this.mail = mail;
    }

    @Override
    protected SendGridResponse doInBackground(Void... voids) {
        try {
            return sendGrid.send(mail).call();
        } catch (Exception e) {
            e.printStackTrace();
            return SendGridResponse.Factory.error(0, e.getMessage());
        }
    }

}
