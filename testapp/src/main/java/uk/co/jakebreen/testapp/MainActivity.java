package uk.co.jakebreen.testapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import uk.co.jakebreen.sendgridandroid.SendGrid;
import uk.co.jakebreen.sendgridandroid.SendGridMail;

public class MainActivity extends AppCompatActivity {

    private String SENDGRID_API_KEY = "***REMOVED***";

    private Button btnSend;
    private SendGrid sendGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = findViewById(R.id.btn_send_mail);
        sendGrid = SendGrid.create(SENDGRID_API_KEY);
        btnSend.setOnClickListener(v -> sendMail());
    }

    @SuppressLint("CheckResult")
    private void sendMail() {
        SendGridMail sendGridMail = new SendGridMail();
        sendGridMail.addTo("jbreendev@gmail.com", "Jake");
        sendGridMail.setFrom("test-app@email.com", "Test App");
        sendGridMail.setContent("Body content");
        sendGridMail.setSubject("Subject hello");
//        sendGridMail.setTemplateId("d-06545327bbcc4cbfb84b5600c708e3d2");

        File file = new File("");
        sendGridMail.addAttachment(file);

        Single.fromCallable(sendGrid.send(sendGridMail))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.d("XXX", "XXX response success=" + s.isSuccessful() + ", code=" + s.getCode() + ", message=" + s.getErrorMessage());
                }, e -> Log.d("XXX", "XXX e=" + e.getMessage()));

    }
}
