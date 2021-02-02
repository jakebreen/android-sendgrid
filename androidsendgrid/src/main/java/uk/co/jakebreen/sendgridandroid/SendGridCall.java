package uk.co.jakebreen.sendgridandroid;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import static uk.co.jakebreen.sendgridandroid.SendGridResponse.Factory.error;
import static uk.co.jakebreen.sendgridandroid.SendGridResponse.Factory.success;

class SendGridCall {

    private static final String BASE_URL = "https://sendgrid.com/v3/";

    Callable<SendGridResponse> call(String url, final String key, final SendGridMailBody body) {
        final String apiUrl = String.format("%s%s", BASE_URL, url);
        return new Callable<SendGridResponse>() {
            @Override
            public SendGridResponse call() throws Exception {
                final URL url1 = new URL(apiUrl);
                final HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", key);
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");

                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(body.getBody().toString().getBytes("UTF-8"));
                outputStream.close();

                InputStream inputStream;
                try {
                    inputStream = urlConnection.getInputStream();
                } catch (IOException exception) {
                    inputStream = urlConnection.getErrorStream();
                }

                int code = urlConnection.getResponseCode();
                String response = SendGridCall.this.readInputStream(inputStream);
                urlConnection.disconnect();

                return createResponse(code, response);
            }
        };
    }

    private String readInputStream(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private SendGridResponse createResponse(int code, String response) {
        if (code >= 200 && code < 300)
            return success(code);
        else
            return error(code, response);
    }

}
