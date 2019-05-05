package uk.co.jakebreen.sendgridandroid;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.co.jakebreen.sendgridandroid.SendGridMail.*;
import static uk.co.jakebreen.sendgridandroid.SendGridMailBody.*;

public class SendGridMailBodyTest {

    private static final String CONTENT_BODY = "Email content body";

    @Mock SendGridMail mail;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void givenListOfToEmails_whenCreatingMailBody_thenReturnJsonBody() throws JSONException, IOException {
        Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        map.put("kate.green@example.com", "Kate Green");
        map.put("will.smith@example.com", "Will Smith");
        when(mail.getTo()).thenReturn(map);

        assertEquals(parseEmails("/json/emails_to"), getToParams(mail).toString());
    }

    @Test
    public void givenToEmail_whenCreatingMailBodyAndNameIsEmpty_thenReturnJsonBodyWithoutNameValue() throws JSONException {
        String expectedValue = "[{\"to\":[{\"email\":\"john.doe@example.com\"}]}]";

        Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", EMPTY);
        when(mail.getTo()).thenReturn(map);

        assertEquals(expectedValue, getToParams(mail).toString());
    }

    @Test
    public void givenFromEmail_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        String expectedValue = "[{\"from\":[{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}]}]";

        Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(map);

        assertEquals(expectedValue, getFromParams(mail).toString());
    }

    @Test
    public void givenListOfCcEmails_whenCreatingMailBody_thenReturnJsonBody() throws JSONException, IOException {
        Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        map.put("kate.green@example.com", "Kate Green");
        map.put("will.smith@example.com", "Will Smith");
        when(mail.getCc()).thenReturn(map);

        assertEquals(parseEmails("/json/emails_cc"), getCcParams(mail).toString());
    }

    @Test
    public void givenListOfBccEmails_whenCreatingMailBody_thenReturnJsonBody() throws JSONException, IOException {
        Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        map.put("kate.green@example.com", "Kate Green");
        map.put("will.smith@example.com", "Will Smith");
        when(mail.getBcc()).thenReturn(map);

        assertEquals(parseEmails("/json/emails_bcc"), getBccParams(mail).toString());
    }

    @Test
    public void givenContent_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        String expectedValue = "[{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}]";

        Map<String, List<String>> map = new HashMap<>();
        map.put(TYPE_PLAIN, Collections.singletonList(CONTENT_BODY));
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail));
    }

    @Test
    public void givenListOfContents_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        String expectedValue = "[{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}," +
                "{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}]";

        Map<String, List<String>> map = new HashMap<>();
        map.put(TYPE_PLAIN, Arrays.asList(CONTENT_BODY, CONTENT_BODY));
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail));
    }

    @Test
    public void givenListOfContentsWithDifferentTypes_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        String expectedValue = "[{\"type\":\"text/html\",\"value\":\"" + CONTENT_BODY + "\"}," +
                "{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}]";

        Map<String, List<String>> map = new HashMap<>();
        map.put(TYPE_PLAIN, Collections.singletonList(CONTENT_BODY));
        map.put(TYPE_HTML, Collections.singletonList(CONTENT_BODY));
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail));
    }

    @Test
    public void xxx() {
        Map<String, String> toMap = new HashMap<>();
        toMap.put("john.doe@example.com", "John Doe");
        toMap.put("kate.green@example.com", "Kate Green");
        toMap.put("will.smith@example.com", "Will Smith");
        when(mail.getTo()).thenReturn(toMap);
        Map<String, String> fromMap = new HashMap<>();
        fromMap.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(fromMap);

        System.out.println(create(mail).getBody());
    }

    private String parseEmails(String file) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(file);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String read;

        while ((read = bufferedReader.readLine()) != null)
            stringBuilder.append(read.trim());

        bufferedReader.close();
        return stringBuilder.toString();
    }

}