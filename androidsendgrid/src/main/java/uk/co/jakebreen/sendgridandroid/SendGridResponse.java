package uk.co.jakebreen.sendgridandroid;

import androidx.annotation.Nullable;

public class SendGridResponse {

    private final int code;
    private final String errorMessage;
    private final Exception exception;

    private SendGridResponse(int code, @Nullable String errorMessage, @Nullable Exception exception) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.exception = exception;
    }

    private static SendGridResponse create(int code, String errorMessage) {
        return new SendGridResponse(code, errorMessage, null);
    }

    private static SendGridResponse create(Exception exception) {
        return new SendGridResponse(0, "", exception);
    }

    /**
     * Return the boolean success state of the response.
     *
     * @return boolean success state
     */
    public boolean isSuccessful() {
        return errorMessage == null;
    }

    /**
     * Returns the HTTP response code for the request.
     *
     * @return HTTP response code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the associated error message of a failed response.
     * NULL when successful.
     *
     * @return response error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    Exception getException() {
        return exception;
    }

    static class Factory {

        static SendGridResponse success(int response) {
            return SendGridResponse.create(response, null);
        }

        static SendGridResponse error(int response, String errorMessage) {
            return SendGridResponse.create(response, ErrorParser.parseError(errorMessage));
        }

        static SendGridResponse error(Exception exception) {
            return SendGridResponse.create(exception);
        }

    }
}
