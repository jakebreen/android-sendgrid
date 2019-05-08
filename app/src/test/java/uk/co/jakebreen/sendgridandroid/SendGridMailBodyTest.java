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

        assertEquals(parseJsonFile("/json/emails_to"), getToParams(mail).toString());
    }

    @Test
    public void givenToEmail_whenCreatingMailBodyAndNameIsEmpty_thenReturnJsonBodyWithoutNameValue() throws JSONException {
        String expectedValue = "{\"to\":[{\"email\":\"john.doe@example.com\"}]}";

        Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", EMPTY);
        when(mail.getTo()).thenReturn(map);

        assertEquals(expectedValue, getToParams(mail).toString());
    }

    @Test
    public void givenFromEmail_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        String expectedValue = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

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

        assertEquals(parseJsonFile("/json/emails_cc"), getCcParams(mail).toString());
    }

    @Test
    public void givenListOfBccEmails_whenCreatingMailBody_thenReturnJsonBody() throws JSONException, IOException {
        Map<String, String> map = new HashMap<>();
        map.put("john.doe@example.com", "John Doe");
        map.put("kate.green@example.com", "Kate Green");
        map.put("will.smith@example.com", "Will Smith");
        when(mail.getBcc()).thenReturn(map);

        assertEquals(parseJsonFile("/json/emails_bcc"), getBccParams(mail).toString());
    }

    @Test
    public void givenContent_whenCreatingMailBody_thenReturnJsonBody() throws JSONException {
        String expectedValue = "[{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}]";

        Map<String, String> map = new HashMap<>();
        map.put(TYPE_PLAIN, CONTENT_BODY);
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail).toString());
    }

    @Test
    public void givenContentsWithPlainTypeThenHtmlType_whenCreatingMailBody_thenReturnJsonBodyInThatOrder() throws JSONException {
        String expectedValue = "[{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}," + "{\"type\":\"text/html\",\"value\":\"" + CONTENT_BODY  + "\"}]";

        Map<String, String> map = new HashMap<>();
        map.put(TYPE_PLAIN, CONTENT_BODY);
        map.put(TYPE_HTML, CONTENT_BODY);
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail).toString());
    }

    @Test
    public void givenContentsWithHtmlTypeThenPlainType_whenCreatingMailBody_thenReturnJsonBodyInCorrectOrder() throws JSONException {
        String expectedValue = "[{\"type\":\"text/plain\",\"value\":\"" + CONTENT_BODY + "\"}," + "{\"type\":\"text/html\",\"value\":\"" + CONTENT_BODY  + "\"}]";

        Map<String, String> map = new HashMap<>();
        map.put(TYPE_HTML, CONTENT_BODY);
        map.put(TYPE_PLAIN, CONTENT_BODY);
        when(mail.getContent()).thenReturn(map);

        assertEquals(expectedValue, getContentParams(mail).toString());
    }

    @Test
    public void xx() {
        SendGridMail mail = new SendGridMail();
        String cat1 = "o6QmbBaW4vxmlJLEFWoQxQvH9ozsTiNbsYui31SqadOKHK1XyUGkIRkjRjW6HksGxfqTOJfo2o5elaPMi6FNiwZ2l4ISzohZ2aEJ4M2TBmAvwUcCzVhcvLcyHVUAd5NbOomBVnv4gGmevRisTzoqrTMbAjVcEBvaWzm7l7WT1HlGrYR7IoLSr94nR9B3c91nqJliN6APMsVi1g7HTLj6HvVCkCKqm5S7wJbIr9ZeuiJ1JnG0W1k0XXRHfY28FuBp";
        mail.addCategory(cat1);
//        when(mail.getCategories()).thenReturn(Collections.singletonList(""));
//        getCategories(mail);
        System.out.println(mail.getCategories());
    }

    @Test
    public void givenMultipleMailParameters_whenCreatingMailBody_thenReturnJsonBody() throws IOException {
        Map<String, String> toMap = new HashMap<>();
        toMap.put("john.doe@example.com", "John Doe");
        toMap.put("kate.green@example.com", "Kate Green");
        toMap.put("will.smith@example.com", "Will Smith");
        when(mail.getTo()).thenReturn(toMap);

        Map<String, String> fromMap = new HashMap<>();
        fromMap.put("john.doe@example.com", "John Doe");
        when(mail.getFrom()).thenReturn(fromMap);

        when(mail.getSubject()).thenReturn("Mail subject");

        Map<String, String> contentMap = new HashMap<>();
        contentMap.put(TYPE_PLAIN, CONTENT_BODY);
        contentMap.put(TYPE_HTML, CONTENT_BODY);
        when(mail.getContent()).thenReturn(contentMap);

        when(mail.getTemplateId()).thenReturn("733ba07f-ead1-41fc-933a-3976baa23716");

        Map<String, String> replyMap = new HashMap<>();
        replyMap.put("no-reply@email.com", "No reply");
        when(mail.getReplyTo()).thenReturn(replyMap);

        assertEquals(parseJsonFile("/json/email"), create(mail).getBody().toString());
    }

    private String parseJsonFile(String file) throws IOException {
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