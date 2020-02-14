package uk.co.jakebreen.sendgridandroid;

import org.jetbrains.annotations.Nullable;

public class SendGridResponse {

    private final int code;
    private final String errorMessage;

    private SendGridResponse(int code, @Nullable String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    private static SendGridResponse create(int code, String errorMessage) {
        return new SendGridResponse(code, errorMessage);
    }

    public boolean isSuccessful() {
        return errorMessage == null;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    static class Factory {

        static SendGridResponse success(int response) {
            return SendGridResponse.create(response, null);
        }

        static SendGridResponse error(int response, String errorMessage) {
            return SendGridResponse.create(response, ErrorParser.parseError(errorMessage));
        }

    }
}
