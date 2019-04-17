package com.baeldung.okhttp;


import MultipartBody.FORM;
import com.baeldung.client.Consts;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class OkHttpPostingLiveTest {
    private static final String BASE_URL = ("http://localhost:" + (Consts.APPLICATION_PORT)) + "/spring-rest";

    private static final String URL_SECURED_BY_BASIC_AUTHENTICATION = "http://browserspy.dk/password-ok.php";

    OkHttpClient client;

    @Test
    public void whenSendPostRequest_thenCorrect() throws IOException {
        final RequestBody formBody = new FormBody.Builder().add("username", "test").add("password", "test").build();
        final Request request = new Request.Builder().url(((OkHttpPostingLiveTest.BASE_URL) + "/users")).post(formBody).build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
        Assert.assertThat(response.code(), Matchers.equalTo(200));
    }

    @Test
    public void whenSendPostRequestWithAuthorization_thenCorrect() throws IOException {
        final String postBody = "test post";
        final Request request = new Request.Builder().url(OkHttpPostingLiveTest.URL_SECURED_BY_BASIC_AUTHENTICATION).addHeader("Authorization", Credentials.basic("test", "test")).post(RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), "test post")).build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
        Assert.assertThat(response.code(), Matchers.equalTo(200));
    }

    @Test
    public void whenPostJson_thenCorrect() throws IOException {
        final String json = "{\"id\":1,\"name\":\"John\"}";
        final RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"id\":1,\"name\":\"John\"}");
        final Request request = new Request.Builder().url(((OkHttpPostingLiveTest.BASE_URL) + "/users/detail")).post(body).build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
        Assert.assertThat(response.code(), Matchers.equalTo(200));
    }

    @Test
    public void whenSendMultipartRequest_thenCorrect() throws IOException {
        final RequestBody requestBody = new MultipartBody.Builder().setType(FORM).addFormDataPart("username", "test").addFormDataPart("password", "test").addFormDataPart("file", "file.txt", RequestBody.create(MediaType.parse("application/octet-stream"), new File("src/test/resources/test.txt"))).build();
        final Request request = new Request.Builder().url(((OkHttpPostingLiveTest.BASE_URL) + "/users/multipart")).post(requestBody).build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
        Assert.assertThat(response.code(), Matchers.equalTo(200));
    }
}
