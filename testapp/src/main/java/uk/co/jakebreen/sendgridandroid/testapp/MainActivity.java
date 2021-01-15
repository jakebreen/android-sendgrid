package uk.co.jakebreen.sendgridandroid.testapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uk.co.jakebreen.sendgridandroid.SendGrid;
import uk.co.jakebreen.sendgridandroid.SendGridMail;
import uk.co.jakebreen.sendgridandroid.SendGridResponse;
import uk.co.jakebreen.testapp.R;

public class MainActivity extends AppCompatActivity {

    private static final String PREFERENCES = "sendgrid_preferences";
    private static final String PREFERENCE_RECIPIENT_EMAIL = "PREFERENCE_RECIPIENT_EMAIL";
    private static final String PREFERENCE_RECIPIENT_NAME = "PREFERENCE_RECIPIENT_NAME";
    private static final String PREFERENCE_SENDER_EMAIL = "PREFERENCE_SENDER_EMAIL";
    private static final String PREFERENCE_SENDER_NAME = "PREFERENCE_SENDER_NAME";
    private static final String PREFERENCE_SUBJECT = "PREFERENCE_SUBJECT";
    private static final String PREFERENCE_CONTENT = "PREFERENCE_CONTENT";

    // Include template ID if required and use 'mail.setDynamicTemplateData()' to include customizations
    private static final String TEMPLATE_ID = "";

    private static final int REQUEST_CODE = 1;

    private Button btnSend, btnAddAttachment, btnClearAttachments;
    private EditText etRecipientEmail, getEtRecipientName;
    private EditText etSenderEmail, getEtSenderName;
    private EditText etSubject, etContent;
    private TextView tvAttachments;

    private SendGrid sendGrid;
    private final List<File> attachments = new ArrayList<>();
    private final List<Uri> uris = new ArrayList<>();
    private Disposable disposable;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        // Add API KEY
        sendGrid = SendGrid.create("<API_KEY>");

        btnSend = findViewById(R.id.btn_send);
        btnAddAttachment = findViewById(R.id.btn_attachment);
        etRecipientEmail = findViewById(R.id.et_recipient_email);
        getEtRecipientName = findViewById(R.id.et_recipient_name);
        etSenderEmail = findViewById(R.id.et_senders_email);
        getEtSenderName = findViewById(R.id.et_senders_name);
        etSubject = findViewById(R.id.et_subject);
        etContent = findViewById(R.id.et_content);
        tvAttachments = findViewById(R.id.tv_attachments);
        btnClearAttachments = findViewById(R.id.btn_clear_attachments);

        btnSend.setOnClickListener(v -> sendMail());
        btnAddAttachment.setOnClickListener(v -> addAttachment());
        btnClearAttachments.setOnClickListener(v -> clearAttachments());

        etRecipientEmail.setText(sharedPreferences.getString(PREFERENCE_RECIPIENT_EMAIL, ""));
        getEtRecipientName.setText(sharedPreferences.getString(PREFERENCE_RECIPIENT_NAME, ""));
        etSenderEmail.setText(sharedPreferences.getString(PREFERENCE_SENDER_EMAIL, ""));
        getEtSenderName.setText(sharedPreferences.getString(PREFERENCE_SENDER_NAME, ""));
        etSubject.setText(sharedPreferences.getString(PREFERENCE_SUBJECT, ""));
        etContent.setText(sharedPreferences.getString(PREFERENCE_CONTENT, ""));
        clearAttachments();
    }

    private void clearAttachments() {
        attachments.clear();
        uris.clear();
        setAttachmentCount();
    }

    private void setAttachmentCount() {
        final int count = attachments.size() + uris.size();
        tvAttachments.setText(String.format("%s Attachments", count));
        if (count >= 10)
            btnAddAttachment.setEnabled(false);
        else
            btnAddAttachment.setEnabled(true);
    }

    private void sendMail() {
        final String recipientEmail = etRecipientEmail.getText().toString();
        final String recipientName = getEtRecipientName.getText().toString();
        final String senderEmail = etSenderEmail.getText().toString();
        final String senderName = getEtSenderName.getText().toString();
        final String subject = etSubject.getText().toString();
        final String content = etContent.getText().toString();

        if (recipientEmail.isEmpty() || senderEmail.isEmpty() || subject.isEmpty() || content.isEmpty()) {
            showMissingField();
            return;
        }

        final SendGridMail mail = new SendGridMail();
        mail.addRecipient(recipientEmail, recipientName);
        mail.setFrom(senderEmail, senderName);
        mail.setSubject(subject);
        mail.setContent(content);

        try {
            if (!attachments.isEmpty())
                for (File file : attachments)
                    mail.addAttachment(file);

            if (!uris.isEmpty())
                for (Uri uri : uris)
                    mail.addAttachment(getApplicationContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!TEMPLATE_ID.isEmpty()) mail.setTemplateId(TEMPLATE_ID);

        send(mail);
    }

    private void send(SendGridMail mail) {
        disposable = Single.fromCallable(sendGrid.send(mail))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onResponse);
    }

    private void onResponse(SendGridResponse response) {
        if (response.isSuccessful())
            Toast.makeText(getApplicationContext(),
                    "Email sent successfully",
                    Toast.LENGTH_SHORT)
                    .show();
        else
            Toast.makeText(getApplicationContext(),
                    "Error " + response.getCode() + " while sending email: " + response.getErrorMessage(),
                    Toast.LENGTH_SHORT)
                    .show();

        clearAttachments();
    }

    private void addAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void showMissingField() {
        Toast.makeText(getApplicationContext(), "Required fields missing", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            uris.add(data.getData());
//            attachments.add(getFileFromUri(data.getData()));
            setAttachmentCount();
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            return FileUtil.from(getApplicationContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }

    @Override
    protected void onStop() {
        super.onStop();
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PREFERENCE_RECIPIENT_EMAIL, etRecipientEmail.getText().toString());
        editor.putString(PREFERENCE_RECIPIENT_NAME, getEtRecipientName.getText().toString());
        editor.putString(PREFERENCE_SENDER_EMAIL, etSenderEmail.getText().toString());
        editor.putString(PREFERENCE_SENDER_NAME, getEtSenderName.getText().toString());
        editor.putString(PREFERENCE_SUBJECT, etSubject.getText().toString());
        editor.putString(PREFERENCE_CONTENT, etContent.getText().toString());

        editor.apply();
    }
}
