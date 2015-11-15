package de.halfreal.googleplayscraper;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestHelper {

    public static Answer<Call> withString(final String content) throws Throwable {
        return new Answer<Call>() {
            @Override
            public Call answer(InvocationOnMock invocation) throws Throwable {
                Call mock = mock(Call.class);
                ResponseBody body = ResponseBody.create(MediaType.parse("text/html"),
                        content);
                com.squareup.okhttp.Response response = new com.squareup.okhttp.Response.Builder()
                        .request((Request) invocation.getArguments()[0])
                        .code(200)
                        .protocol(Protocol.HTTP_1_1)
                        .body(body)
                        .build();
                when(mock.execute()).thenReturn(response);
                return mock;
            }
        };
    }

    public static Answer<Call> withFile(final String file) throws Throwable {
        return withString(fromFile(file));
    }

    public static String fromFile(String file) throws URISyntaxException, IOException {
        java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(file);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
    }
}
