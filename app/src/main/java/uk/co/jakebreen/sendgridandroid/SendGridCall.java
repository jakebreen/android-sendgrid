package uk.co.jakebreen.sendgridandroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;

class SendGridCall {

    private static final String BASE_URL = "https://sendgrid.com/v3/";

    Callable<SendGridResponse> call(String url, final String key) {
        final String apiUrl = String.format("%s%s", BASE_URL, url);
        return new Callable<SendGridResponse>() {
            @Override
            public SendGridResponse call() throws IOException {
                final URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", key);

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    data = inputStreamReader.read();
                    System.out.print(current);
                }

                urlConnection.disconnect();

                return new SendGridResponse();
            }
        };
    }

    private void XXX(Map<String, String> params) {
        StringBuilder sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

}
