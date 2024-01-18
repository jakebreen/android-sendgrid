package uk.co.jakebreen.sendgridandroid.testapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.disposables.Disposable;
import uk.co.jakebreen.sendgridandroid.SendGrid;
import uk.co.jakebreen.sendgridandroid.SendGridMail;
import uk.co.jakebreen.sendgridandroid.SendGridResponse;
import uk.co.jakebreen.sendgridandroid.SendTask;
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
    private EditText etRecipientEmail, etRecipientName;
    private EditText etSenderEmail, etSenderName;
    private EditText etSubject, etContent;
    private EditText etReplyToEmail, getEtReplyToName;
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
        etRecipientName = findViewById(R.id.et_recipient_name);
        etSenderEmail = findViewById(R.id.et_senders_email);
        etSenderName = findViewById(R.id.et_senders_name);
        etSubject = findViewById(R.id.et_subject);
        etContent = findViewById(R.id.et_content);
        etReplyToEmail = findViewById(R.id.et_reply_to_email);
        getEtReplyToName = findViewById(R.id.et_reply_to_name);
        tvAttachments = findViewById(R.id.tv_attachments);
        btnClearAttachments = findViewById(R.id.btn_clear_attachments);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.sendMail();
            }
        });
        btnAddAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.addAttachment();
            }
        });
        btnClearAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.clearAttachments();
            }
        });

        etRecipientEmail.setText(sharedPreferences.getString(PREFERENCE_RECIPIENT_EMAIL, ""));
        etRecipientName.setText(sharedPreferences.getString(PREFERENCE_RECIPIENT_NAME, ""));
        etSenderEmail.setText(sharedPreferences.getString(PREFERENCE_SENDER_EMAIL, ""));
        etSenderName.setText(sharedPreferences.getString(PREFERENCE_SENDER_NAME, ""));
        etSubject.setText(sharedPreferences.getString(PREFERENCE_SUBJECT, ""));
        etContent.setText(sharedPreferences.getString(PREFERENCE_CONTENT, ""));

        etSenderEmail.setText("android-sendgrid@testapp.com");
        etSenderName.setText("Test App");
        etRecipientName.setText("Recipient");
        etSubject.setText("Test App Subject");

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
        final String recipientName = etRecipientName.getText().toString();
        final String senderEmail = etSenderEmail.getText().toString();
        final String senderName = etSenderName.getText().toString();
        final String subject = etSubject.getText().toString();
        final String content = etContent.getText().toString();
        final String replyToEmail = etReplyToEmail.getText().toString();
        final String replyToName = getEtReplyToName.getText().toString();

        if (recipientEmail.isEmpty() || senderEmail.isEmpty() || subject.isEmpty()) {
            showMissingField();
            return;
        }

        final SendGridMail mail = new SendGridMail();
        mail.addRecipient(recipientEmail, recipientName);
        mail.setFrom(senderEmail, senderName);
        mail.setSubject(subject);

        if (!content.isEmpty()) mail.setContent(content);

        if (!replyToEmail.equals("")) {
            mail.setReplyTo(replyToEmail, replyToName);
        }

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

        if (!TEMPLATE_ID.isEmpty()) {
            mail.setTemplateId(TEMPLATE_ID);
//            try {
//                mail.setDynamicTemplateData(getTemplatePayload());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }

        send(mail);
    }

    private JSONObject getTemplatePayload() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("field_one", "Hello this is field one");
        jsonObject.put("field_two", "Hello this is field two");
        jsonObject.put("field_three", "Hello this is field three");
        jsonObject.put("image_text", "Hello this is image text");
        return jsonObject;
    }

    private void send(SendGridMail mail) {
        final SendTask task = new SendTask(sendGrid);
        try {
            SendGridResponse response = task.send(mail);
            onResponse(response);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        disposable = Single.fromCallable(sendGrid.send(mail))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::onResponse);
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
        editor.putString(PREFERENCE_RECIPIENT_NAME, etRecipientName.getText().toString());
        editor.putString(PREFERENCE_SENDER_EMAIL, etSenderEmail.getText().toString());
        editor.putString(PREFERENCE_SENDER_NAME, etSenderName.getText().toString());
        editor.putString(PREFERENCE_SUBJECT, etSubject.getText().toString());
        editor.putString(PREFERENCE_CONTENT, etContent.getText().toString());

        editor.apply();
    }
}
