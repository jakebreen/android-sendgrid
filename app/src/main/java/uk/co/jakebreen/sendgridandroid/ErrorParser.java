package uk.co.jakebreen.sendgridandroid;

import org.json.JSONException;
import org.json.JSONObject;

class ErrorParser {

    private static final String KEY_ERRORS = "errors";
    private static final String KEY_MESSAGE = "message";

    static String parseError(String response) {
        try {
            return new JSONObject(response).getJSONArray(KEY_ERRORS).getJSONObject(0).get(KEY_MESSAGE).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return String.format("Error parsing error message: %s", e.getMessage());
        }
    }

}
