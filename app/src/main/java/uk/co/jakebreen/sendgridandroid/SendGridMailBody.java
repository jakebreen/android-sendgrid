package uk.co.jakebreen.sendgridandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static uk.co.jakebreen.sendgridandroid.SendGridMail.EMPTY;

class SendGridMailBody {

    private static final String BODY_PERSONALIZATIONS = "personalizations";
    private static final String BODY_TO = "to";
    private static final String BODY_FROM = "from";
    private static final String BODY_CC = "cc";
    private static final String BODY_BCC = "bcc";
    private static final String BODY_SUBJECT = "subject";
    private static final String BODY_CONTENT = "content";

    private static final String PARAMS_EMAIL = "email";
    private static final String PARAMS_NAME = "name";
    private static final String PARAMS_CONTENT_TYPE = "type";
    private static final String PARAMS_CONTENT_VALUE = "value";

//    private Map<String, String> body;
    private JSONArray body;

    private SendGridMailBody(JSONArray body) {
        this.body = body;
    }

    static SendGridMailBody create(SendGridMail mail) {
        JSONArray body = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(BODY_PERSONALIZATIONS, getToParams(mail));
            body.put(jsonObject);
            body.put(getFromParams(mail));
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        body.put(BODY_TO, getToParams(mail));

//        try {
//            Map<String, String> toMap = new HashMap<>();
//            toMap.put(BODY_TO, getToParams(mail));

//            body.put(BODY_PERSONALIZATIONS, setPersonalizations());

//            body.put(BODY_CC, getToParams(mail));

//            body.put(BODY_CC, getCcParams(mail));
//            body.put(BODY_BCC, getBccParams(mail));
//            body.put(BODY_FROM, getFromParams(mail));
//            body.put(BODY_SUBJECT, getSubjectParams(mail));
//            body.put(BODY_CONTENT, getContentParams(mail));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return new SendGridMailBody(body);
    }

//    private static String setPersonalizations(String params) throws JSONException {
//        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(BODY_PERSONALIZATIONS, params);
//        jsonArray.put(jsonObject);
//        return params;
//    }

    JSONArray getBody() {
        return body;
    }

    static String getContentParams(SendGridMail mail) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Entry<String, List<String>> set : mail.getContent().entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_CONTENT_TYPE, set.getKey());
            for (String bodies :set.getValue()) {
                jsonObject.put(PARAMS_CONTENT_VALUE, bodies);
                jsonArray.put(jsonObject);
            }
        }
        return jsonArray.toString();
    }

    static String getSubjectParams(SendGridMail mail) {
        return mail.getSubject();
    }

    static JSONArray getBccParams(SendGridMail mail) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_BCC, convertEmails(mail.getBcc()));
        return jsonArray.put(jsonObject);
    }

    static JSONArray getCcParams(SendGridMail mail) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_CC, convertEmails(mail.getCc()));
        return jsonArray.put(jsonObject);
    }

    static JSONArray getToParams(SendGridMail mail) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_TO, convertEmails(mail.getTo()));
        return jsonArray.put(jsonObject);
    }

    static JSONArray getFromParams(SendGridMail mail) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_FROM, convertEmails(mail.getFrom()));
        return jsonArray.put(jsonObject);
//        return jsonArray.put(jsonObject);
    }

    private static JSONArray convertEmails(Map<String, String> emailMap) throws JSONException {
        int count = 0;
        JSONArray jsonArray = new JSONArray();
        for (Entry<String, String> set : emailMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue().equals(EMPTY) ? null : set.getValue());
            jsonArray.put(jsonObject);

            count ++;
            if (count == 1000) break;
        }
        return jsonArray;
    }
}
