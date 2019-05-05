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
    private static final String BODY_TEMPLATE_ID = "template_id";
    private static final String BODY_REPLY_TO = "reply_to";

    private static final String PARAMS_EMAIL = "email";
    private static final String PARAMS_NAME = "name";
    private static final String PARAMS_CONTENT_TYPE = "type";
    private static final String PARAMS_CONTENT_VALUE = "value";

    private JSONObject body;

    private SendGridMailBody(JSONObject body) {
        this.body = body;
    }

    static SendGridMailBody create(SendGridMail mail) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(getToParams(mail));
            jsonArray.put(getCcParams(mail));
            jsonArray.put(getBccParams(mail));
            jsonArray.put(getSubjectParams(mail));
            jsonObject.put(BODY_PERSONALIZATIONS, jsonArray);
            jsonObject.put(BODY_FROM, getFromParams(mail));
            jsonObject.put(BODY_CONTENT, getContentParams(mail));
            jsonObject.put(BODY_TEMPLATE_ID, getTemplateId(mail));
            jsonObject.put(BODY_REPLY_TO, getReplyToParams(mail));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new SendGridMailBody(jsonObject);
    }

    JSONObject getBody() {
        return body;
    }

    static JSONArray getContentParams(SendGridMail mail) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Entry<String, List<String>> set : mail.getContent().entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_CONTENT_TYPE, set.getKey());
            for (String bodies :set.getValue()) {
                jsonObject.put(PARAMS_CONTENT_VALUE, bodies);
                jsonArray.put(jsonObject);
            }
        }
        return jsonArray;
    }

    static JSONObject getSubjectParams(SendGridMail mail) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_SUBJECT, mail.getSubject());
        return jsonObject;
    }

    static String getTemplateId(SendGridMail mail) {
        return mail.getTemplateId();
    }

    static JSONObject getBccParams(SendGridMail mail) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_BCC, convertEmails(mail.getBcc()));
        return jsonObject;
    }

    static JSONObject getCcParams(SendGridMail mail) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_CC, convertEmails(mail.getCc()));
        return jsonObject;
    }

    static JSONObject getToParams(SendGridMail mail) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BODY_TO, convertEmails(mail.getTo()));
        return jsonObject;
    }

    static JSONObject getFromParams(SendGridMail mail) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (Entry<String, String> set : mail.getFrom().entrySet()) {
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue());
        }
        return jsonObject;
    }

    static JSONObject getReplyToParams(SendGridMail mail) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (Entry<String, String> set : mail.getReplyTo().entrySet()) {
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue());
        }
        return jsonObject;
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
