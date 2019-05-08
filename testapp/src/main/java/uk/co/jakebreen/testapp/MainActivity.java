package uk.co.jakebreen.testapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
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
//        sendGridMail.addTo(" ", "Greg");
        sendGridMail.setFrom("no-reply@email.com", "No Reply");
        sendGridMail.setContent("Body content");
        sendGridMail.setSubject("Subject hello");

        String cat1 = "o6QmbBaW4vxmlJLEFWoQxQvH9ozsTiNbsYui31SqadOKHK1XyUGkIRkjRjW6HksGxfqTOJfo2o5elaPMi6FNiwZ2l4ISzohZ2aEJ4M2TBmAvwUcCzVhcvLcyHVUAd5NbOomBVnv4gGmevRisTzoqrTMbAjVcEBvaWzm7l7WT1HlGrYR7IoLSr94nR9B3c91nqJliN6APMsVi1g7HTLj6HvVCkCKqm5S7wJbIr9ZeuiJ1JnG0W1k0XXRHfY28FuBp";
        String cat2 = "cat 2";
        String cat3 = "cat 3";
        sendGridMail.addCategory(cat1);
        sendGridMail.addCategory(cat2);
        sendGridMail.addCategory(cat3);

        Single.fromCallable(sendGrid.send(sendGridMail))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.d("XXX", "XXX response success=" + s.isSuccessful() + ", code=" + s.getCode() + ", message=" + s.getErrorMessage());
                }, e -> Log.d("XXX", "XXX e=" + e.getMessage()));

    }
}
