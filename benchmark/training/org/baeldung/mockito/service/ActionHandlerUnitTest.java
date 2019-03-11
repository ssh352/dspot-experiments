package org.baeldung.mockito.service;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ActionHandlerUnitTest {
    @Mock
    private Service service;

    @Captor
    private ArgumentCaptor<Callback<Response>> callbackCaptor;

    @Test
    public void givenServiceWithValidResponse_whenCallbackReceived_thenProcessed() {
        ActionHandler handler = new ActionHandler(service);
        handler.doAction();
        Mockito.verify(service).doAction(ArgumentMatchers.anyString(), callbackCaptor.capture());
        Callback<Response> callback = callbackCaptor.getValue();
        Response response = new Response();
        callback.reply(response);
        String expectedMessage = "Successful data response";
        Data data = response.getData();
        Assert.assertEquals("Should receive a successful message: ", expectedMessage, data.getMessage());
    }

    @Test
    public void givenServiceWithInvalidResponse_whenCallbackReceived_thenNotProcessed() {
        Response response = new Response();
        response.setIsValid(false);
        Mockito.doAnswer(((Answer<Void>) (( invocation) -> {
            Callback<Response> callback = invocation.getArgument(1);
            callback.reply(response);
            Data data = response.getData();
            Assert.assertNull("No data in invalid response: ", data);
            return null;
        }))).when(service).doAction(ArgumentMatchers.anyString(), ArgumentMatchers.any(Callback.class));
        ActionHandler handler = new ActionHandler(service);
        handler.doAction();
    }
}

