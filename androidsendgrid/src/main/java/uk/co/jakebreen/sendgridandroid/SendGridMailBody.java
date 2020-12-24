package uk.co.jakebreen.sendgridandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Map.Entry;

import uk.co.jakebreen.sendgridandroid.SendGridMail.Attachment;

import static uk.co.jakebreen.sendgridandroid.SendGridMail.EMPTY;
import static uk.co.jakebreen.sendgridandroid.SendGridMail.TYPE_HTML;
import static uk.co.jakebreen.sendgridandroid.SendGridMail.TYPE_PLAIN;

class SendGridMailBody {

    private static final String BODY_PERSONALISATIONS = "personalizations";
    private static final String BODY_TO = "to";
    private static final String BODY_CC = "cc";
    private static final String BODY_BCC = "bcc";
    private static final String BODY_FROM = "from";
    private static final String BODY_SUBJECT = "subject";
    private static final String BODY_CONTENT = "content";
    private static final String BODY_TEMPLATE_ID = "template_id";
    private static final String BODY_REPLY_TO = "reply_to";
    private static final String BODY_SEND_AT = "send_at";
    private static final String BODY_ATTACHMENTS = "attachments";
    private static final String BODY_TRACKING_SETTINGS = "tracking_settings";
    private static final String BODY_DYNAMIC_TEMPLATE_DATA = "dynamic_template_data";

    private static final String PARAMS_EMAIL = "email";
    private static final String PARAMS_NAME = "name";
    private static final String PARAMS_CONTENT_TYPE = "type";
    private static final String PARAMS_CONTENT_VALUE = "value";
    private static final String PARAMS_ATTACHMENT_CONTENT = "content";
    private static final String PARAMS_ATTACHMENT_FILENAME = "filename";

    private final JSONObject body;

    private SendGridMailBody(JSONObject body) {
        this.body = body;
    }

    static SendGridMailBody create(SendGridMail mail) {
        return new SendGridMailBody(createMailBody(mail));
    }

    private static JSONObject createMailBody(SendGridMail mail) {
        final JSONObject parent = new JSONObject();
        try {
            final JSONArray personalization = new JSONArray();
            final JSONObject personalizationObj = new JSONObject();
            personalizationObj.put(BODY_TO, getEmailsArray(mail.getRecipients()));
            if (!mail.getRecipientCarbonCopies().isEmpty())
                personalizationObj.put(BODY_CC, getEmailsArray(mail.getRecipientCarbonCopies()));
            if (!mail.getRecipientBlindCarbonCopies().isEmpty())
                personalizationObj.put(BODY_BCC, getEmailsArray(mail.getRecipientBlindCarbonCopies()));
            if (mail.getDynamicTemplateData() != null)
                personalizationObj.put(BODY_DYNAMIC_TEMPLATE_DATA, mail.getDynamicTemplateData());
            personalization.put(personalizationObj);

            parent.put(BODY_PERSONALISATIONS, personalization);
            parent.put(BODY_FROM, getFromParams(mail));
            parent.put(BODY_SUBJECT, getSubjectParams(mail));
            parent.put(BODY_CONTENT, getContentParams(mail));
            if (mail.getTemplateId() != null)
                parent.put(BODY_TEMPLATE_ID, getTemplateId(mail));
            if (!mail.getReplyTo().isEmpty())
                parent.put(BODY_REPLY_TO, getReplyToParams(mail));
            if (mail.getSendAt() != 0)
                parent.put(BODY_SEND_AT, getSendAt(mail));
            if (mail.getAttachments().size() > 0)
                parent.put(BODY_ATTACHMENTS, getAttachments(mail));
            if (mail.getTrackingSettings().size() > 0) {
                parent.put(BODY_TRACKING_SETTINGS, getTrackingSettings(mail));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parent;
    }

    JSONObject getBody() {
        return body;
    }

    static JSONArray getContentParams(SendGridMail mail) throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        Map<String, String> contentMap = mail.getContent();
        if (contentMap.containsKey(TYPE_PLAIN)) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_CONTENT_TYPE, TYPE_PLAIN);
            jsonObject.put(PARAMS_CONTENT_VALUE, contentMap.get(TYPE_PLAIN));
            jsonArray.put(jsonObject);
            contentMap.remove(TYPE_PLAIN);
        }

        if (contentMap.containsKey(TYPE_HTML)) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_CONTENT_TYPE, TYPE_HTML);
            jsonObject.put(PARAMS_CONTENT_VALUE, contentMap.get(TYPE_HTML));
            jsonArray.put(jsonObject);
            contentMap.remove(TYPE_HTML);
        }

        for (Entry<String, String> set : contentMap.entrySet()) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_CONTENT_TYPE, set.getKey());
            jsonObject.put(PARAMS_CONTENT_VALUE, set.getValue());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    static String getSubjectParams(SendGridMail mail) {
        return mail.getSubject();
    }

    static String getTemplateId(SendGridMail mail) {
        return mail.getTemplateId();
    }

    static JSONObject getFromParams(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        for (Entry<String, String> set : mail.getFrom().entrySet()) {
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue());
        }
        return jsonObject;
    }

    static JSONObject getReplyToParams(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        for (Entry<String, String> set : mail.getReplyTo().entrySet()) {
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue());
        }
        return jsonObject;
    }

    static int getSendAt(SendGridMail mail) {
        return mail.getSendAt();
    }

    static JSONArray getAttachments(SendGridMail mail) throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        for (Attachment attachment : mail.getFileAttachments()) {
            if (attachment.getContent().isEmpty())
                continue;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_ATTACHMENT_CONTENT, attachment.getContent());
            jsonObject.put(PARAMS_ATTACHMENT_FILENAME, attachment.getFilename());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    static JSONObject getTrackingSettings(SendGridMail mail) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        for (Entry<String, Map<String, Boolean>> set : mail.getTrackingSettings().entrySet()) {
            System.out.println(set.getKey());
            System.out.println(set.getValue());
            jsonObject.put(set.getKey(), set.getValue());
        }
        System.out.println(jsonObject);
        return jsonObject;
    }

    static JSONArray getEmailsArray(Map<String, String> emailMap) throws JSONException {
        int count = 0;
        final JSONArray jsonArray = new JSONArray();
        for (Entry<String, String> set : emailMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PARAMS_EMAIL, set.getKey());
            jsonObject.put(PARAMS_NAME, set.getValue().equals(EMPTY) ? null : set.getValue());
            jsonArray.put(jsonObject);

            count ++;
            if (count >= 1000) break;
        }
        return jsonArray;
    }
}
