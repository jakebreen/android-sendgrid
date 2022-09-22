package uk.co.jakebreen.sendgridandroid;

import android.os.AsyncTask;

class SendTaskAsync extends AsyncTask<Void, Void, SendGridResponse> {

    private final SendGrid sendGrid;
    private final SendGridMail mail;

    SendTaskAsync(SendGrid sendGrid, SendGridMail mail) {
        this.sendGrid = sendGrid;
        this.mail = mail;
    }

    @Override
    protected SendGridResponse doInBackground(Void... voids) {
        try {
            return sendGrid.send(mail).call();
        } catch (Exception e) {
            return SendGridResponse.Factory.error(e);
        }
    }

}
