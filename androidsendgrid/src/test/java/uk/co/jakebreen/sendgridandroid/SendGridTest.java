package uk.co.jakebreen.sendgridandroid;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.Callable;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.co.jakebreen.sendgridandroid.ErrorParser.parseError;

public class SendGridTest {

    private static final int RESPONSE_202 = 202;
    private static final int RESPONSE_401 = 401;
    private static final String API_KEY = "api_key";

    @Mock SendGridMail mail;
    @Mock SendGridCall api;
    @Mock Callable<SendGridResponse> callable;
    @Mock SendGridResponse response;

    private SendGrid sendGrid;
    private AutoCloseable autoCloseable;

    @Before
    public void setup() {
        autoCloseable = openMocks(this);
        sendGrid = SendGrid.create(API_KEY);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void givenSendMailRequest_whenRequestIsSuccessful_thenReturnSuccessfulResponse() throws Exception {
        when(response.getCode()).thenReturn(RESPONSE_202);
        when(response.isSuccessful()).thenReturn(true);
        when(callable.call()).thenReturn(response);
        when(api.call(anyString(), anyString(), any(SendGridMailBody.class))).thenReturn(callable);

        sendGrid.send(mail);

        assertEquals(callable.call().getCode(), RESPONSE_202);
        assertTrue(callable.call().isSuccessful());
    }

    @Test
    public void givenSendMailRequest_whenRequestIsUnsuccessfulWithError_thenReturnUnsuccessfulResponse() throws Exception {
        String exampleResponse = "{\"errors\":[{\"message\":\"Does not contain a valid address.\"," +
                "\"field\":\"personalizations.0.to.0.email\"," +
                "\"help\":\"http://sendgrid.com/docs/API_Reference/Web_API_v3/Mail/errors.html#message.personalizations.to\"}]}";

        when(response.getCode()).thenReturn(RESPONSE_401);
        when(response.isSuccessful()).thenReturn(false);
        when(response.getErrorMessage()).thenReturn("Does not contain a valid address.");
        when(callable.call()).thenReturn(response);
        when(api.call(anyString(), anyString(), any(SendGridMailBody.class))).thenReturn(callable);

        sendGrid.send(mail);

        assertEquals(callable.call().getCode(), RESPONSE_401);
        assertFalse(callable.call().isSuccessful());
        assertEquals(callable.call().getErrorMessage(), parseError(exampleResponse));
    }

}