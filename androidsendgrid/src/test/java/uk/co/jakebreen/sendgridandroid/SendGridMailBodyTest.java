package uk.co.jakebreen.sendgridandroid;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.co.jakebreen.sendgridandroid.SendGridMail.*;
import static uk.co.jakebreen.sendgridandroid.SendGridMailBody.*;

public class SendGridMailBodyTest {

    private static final String CONTENT_BODY = "Email content body";

    @Mock SendGridMail mail;

    private AutoCloseable autoCloseable;

    @Before
    public void setup() {
        autoCloseable = openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void givenListOfToEmails_whenCreatingMailBody_thenReturnJsonBody() throws JSONException, IOException {
        final Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        map.put("kate.green@example.com", "Kate Green");
        map.put("will.smith@example.com", "Will Smith");
        when(mail.getRecipients()).thenReturn(map);

        assertEquals(parseJsonFile("/json/emails_array"), getEmailsArray(mail.getRecipients()).toString());
    }

    @Test
    public void givenListOfCcEmails_whenCreatingMailBody_thenReturnJsonBody() throws JSONException, IOException {
        final Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        map.put("kate.green@example.com", "Kate Green");
        map.put("will.smith@example.com", "Will Smith");
        when(mail.getRecipientCarbonCopies()).thenReturn(map);

        assertEquals(parseJsonFile("/json/emails_array"), getEmailsArray(mail.getRecipientCarbonCopies()).toString());
    }

    @Test
    public void givenListOfBccEmails_whenCreatingMailBody_thenReturnJsonBody() throws JSONException, IOException {
        final Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        map.put("kate.green@example.com", "Kate Green");
        map.put("will.smith@example.com", "Will Smith");
        when(mail.getRecipientBlindCarbonCopies()).thenReturn(map);

        assertEquals(parseJsonFile("/json/emails_array"), getEmailsArray(mail.getRecipientBlindCarbonCopies()).toString());
    }

    @Test
    public void givenToEmail_whenCreatingMailBodyAndNameIsEmpty_thenReturnJsonBodyWithoutNameValue() throws JSONException {
        final String expectedValue = "[{\"email\":\"john.doe@example.com\"}]";

        final Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", EMPTY);
        when(mail.getRecipients()).thenReturn(map);

        assertEquals(expectedValue, getEmailsArray(mail.getRecipients()).toString());
    }

    @Test
    public void givenFromEmail_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        final String expectedValue = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        final Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(map);

        assertEquals(expectedValue, getFromParams(mail).toString());
    }

    @Test
    public void givenReplyToEmail_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        final String expectedValue = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        final Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        when(mail.getReplyTo()).thenReturn(map);

        assertEquals(expectedValue, getReplyToParams(mail).toString());
    }

    @Test
    public void givenPlainContent_whenCreatingMailBody_thenPlainContentAdded_andNoHtmlContent() throws JSONException {
        final String expectedValue = "[{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}]";

        final Map<String, String> map = new HashMap<>();
        map.put(TYPE_PLAIN, CONTENT_BODY);
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail).toString());
    }

    @Test
    public void givenHtmlContent_whenCreatingMailBody_thenHtmlContentAdded_andNoPlainContent() throws JSONException {
        final String expectedValue = "[{\"type\":\"text/html\",\"value\":\"" + CONTENT_BODY + "\"}]";

        final Map<String, String> map = new HashMap<>();
        map.put(TYPE_HTML, CONTENT_BODY);
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail).toString());
    }

    @Test
    public void givenContentsWithPlainTypeThenHtmlType_whenCreatingMailBody_thenReturnJsonBodyInThatOrder() throws JSONException {
        final String expectedValue = "[{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}," + "{\"type\":\"text/html\",\"value\":\"" + CONTENT_BODY  + "\"}]";

        final Map<String, String> map = new HashMap<>();
        map.put(TYPE_PLAIN, CONTENT_BODY);
        map.put(TYPE_HTML, CONTENT_BODY);
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail).toString());
    }

    @Test
    public void givenContentsWithHtmlTypeThenPlainType_whenCreatingMailBody_thenReturnJsonBodyInCorrectOrder() throws JSONException {
        final String expectedValue = "[{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}," + "{\"type\":\"text/html\",\"value\":\"" + CONTENT_BODY  + "\"}]";

        final Map<String, String> map = new HashMap<>();
        map.put(TYPE_HTML, CONTENT_BODY);
        map.put(TYPE_PLAIN, CONTENT_BODY);
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail).toString());
    }

    @Test
    public void givenAttachment_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        final String expectedValue = "[{\"filename\":\"TestFile.txt\",\"content\":\"dgFAtXCDASfghjgj4\"}]";

        final SendGridMail.Attachment attachment = mock(SendGridMail.Attachment.class);
        when(attachment.getContent()).thenReturn("dgFAtXCDASfghjgj4");
        when(attachment.getFilename()).thenReturn("TestFile.txt");

        final List<SendGridMail.Attachment> map = new ArrayList<>();
        map.add(attachment);

        when(mail.getFileAttachments()).thenReturn(map);

        assertEquals(expectedValue, getAttachments(mail).toString());
    }

    @Test
    public void givenMultipleMailParameters_whenCreatingMailBody_thenReturnJsonBody() throws IOException {
        final Map<String, String> toMap = new HashMap<>();
        toMap.put("john.doe@example.com", "John Doe");
        toMap.put("kate.green@example.com", "Kate Green");
        toMap.put("will.smith@example.com", "Will Smith");
        when(mail.getRecipients()).thenReturn(toMap);

        final Map<String, String> ccMap = new HashMap<>();
        ccMap.put("john.doe@example.com", "John Doe");
        ccMap.put("kate.green@example.com", "Kate Green");
        ccMap.put("will.smith@example.com", "Will Smith");
        when(mail.getRecipientCarbonCopies()).thenReturn(ccMap);

        final Map<String, String> bccMap = new HashMap<>();
        bccMap.put("john.doe@example.com", "John Doe");
        bccMap.put("kate.green@example.com", "Kate Green");
        bccMap.put("will.smith@example.com", "Will Smith");
        when(mail.getRecipientBlindCarbonCopies()).thenReturn(bccMap);

        final Map<String, String> fromMap = new HashMap<>();
        fromMap.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(fromMap);

        when(mail.getSubject()).thenReturn("Mail subject");

        final Map<String, String> contentMap = new HashMap<>();
        contentMap.put(TYPE_PLAIN, CONTENT_BODY);
        contentMap.put(TYPE_HTML, CONTENT_BODY);
        when(mail.getContent()).thenReturn(contentMap);

        when(mail.getTemplateId()).thenReturn("733ba07f-ead1-41fc-933a-3976baa23716");

        final Map<String, String> replyMap = new HashMap<>();
        replyMap.put("no-reply@email.com", "No reply");
        when(mail.getReplyTo()).thenReturn(replyMap);

        assertEquals(parseJsonFile("/json/email"), create(mail).getBody().toString());
    }

    @Test
    public void givenAllTrackingSettingsEnabled_whenCreatingMailBody_thenReturnJsonBody() throws IOException {
        final Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        map.put("kate.green@example.com", "Kate Green");
        map.put("will.smith@example.com", "Will Smith");
        when(mail.getRecipients()).thenReturn(map);

        final Map<String, Map<String, Boolean>> trackingSettings = new HashMap<>();
        trackingSettings.put(TRACKING_SETTING_CLICK_TRACKING, new HashMap<String, Boolean>() {{ put(TRACKING_SETTING_ENABLED, true); }});
        trackingSettings.put(TRACKING_SETTING_OPEN_TRACKING, new HashMap<String, Boolean>() {{ put(TRACKING_SETTING_ENABLED, true); }});
        trackingSettings.put(TRACKING_SETTING_SUBSCRIPTION_TRACKING, new HashMap<String, Boolean>() {{ put(TRACKING_SETTING_ENABLED, true); }});
        when(mail.getTrackingSettings()).thenReturn(trackingSettings);

        assertEquals(parseJsonFile("/json/emails_tracking_settings_enabled"), create(mail).getBody().toString());
    }

    @Test
    public void givenDynamicTemplateVariables_whenCreatingMailBody_thenReturnJsonBody() throws IOException, JSONException {
        final Map<String, String> toMap = new HashMap<>();
        toMap.put("example@sendgrid.net", EMPTY);
        when(mail.getRecipients()).thenReturn(toMap);

        when(mail.getTemplateId()).thenReturn("733ba07f-ead1-41fc-933a-3976baa23716");

        final JSONObject dynamicTemplateData = new JSONObject();
        dynamicTemplateData.put("forename", "Englebert");
        dynamicTemplateData.put("surname", "Humperdink");
        dynamicTemplateData.put("age", 85);
        dynamicTemplateData.put("retired", true);
        when(mail.getDynamicTemplateData()).thenReturn(dynamicTemplateData);

        final Map<String, String> contentMap = new HashMap<>();
        contentMap.put(TYPE_PLAIN, CONTENT_BODY);
        contentMap.put(TYPE_HTML, CONTENT_BODY);
        when(mail.getContent()).thenReturn(contentMap);

        final Map<String, String> fromMap = new HashMap<>();
        fromMap.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(fromMap);

        assertEquals(parseJsonFile("/json/email_dynamic_template"), create(mail).getBody().toString());
    }

    @Test
    public void givenDynamicTemplate_whenContentNotIncluded_thenReturnJsonBodyWithEmptyContentBlock() throws IOException {
        final Map<String, String> toMap = new HashMap<>();
        toMap.put("example@sendgrid.net", EMPTY);
        when(mail.getRecipients()).thenReturn(toMap);

        final Map<String, String> fromMap = new HashMap<>();
        fromMap.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(fromMap);

        assertEquals(parseJsonFile("/json/email_content_empty"), create(mail).getBody().toString());
    }

    @Test
    public void givenPlainContent_thenNoHtmlContent() throws IOException {
        final Map<String, String> toMap = new HashMap<>();
        toMap.put("example@sendgrid.net", EMPTY);
        when(mail.getRecipients()).thenReturn(toMap);

        final Map<String, String> fromMap = new HashMap<>();
        fromMap.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(fromMap);

        final Map<String, String> contentMap = new HashMap<>();
        contentMap.put(TYPE_PLAIN, CONTENT_BODY);
        when(mail.getContent()).thenReturn(contentMap);

        assertEquals(parseJsonFile("/json/email_content_html_empty"), create(mail).getBody().toString());
    }

    @Test
    public void givenHtmlContent_thenNoPlainContent() throws IOException {
        final Map<String, String> toMap = new HashMap<>();
        toMap.put("example@sendgrid.net", EMPTY);
        when(mail.getRecipients()).thenReturn(toMap);

        final Map<String, String> fromMap = new HashMap<>();
        fromMap.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(fromMap);

        final Map<String, String> contentMap = new HashMap<>();
        contentMap.put(TYPE_HTML, CONTENT_BODY);
        when(mail.getContent()).thenReturn(contentMap);

        assertEquals(parseJsonFile("/json/email_content_plain_empty"), create(mail).getBody().toString());
    }

    private String parseJsonFile(String file) throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream(file);
        final StringBuilder stringBuilder = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String read;

        while ((read = bufferedReader.readLine()) != null)
            stringBuilder.append(read.trim());

        bufferedReader.close();
        return stringBuilder.toString();
    }

}