import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import de.halfreal.googleplayscraper.model.App;
import de.halfreal.googleplayscraper.model.GooglePlayDataFactory;
import de.halfreal.googleplayscraper.service.GooglePlayService;
import retrofit.Retrofit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GooglePlayServiceTest {

    public static final String BASE_URL = "http://test.com";

    private GooglePlayService m_service;
    @Spy
    private OkHttpClient m_client;
    private ArgumentCaptor<Request> m_requestArgumentCaptor;

    @Before
    public void setUp() throws Throwable {
        m_client = new OkHttpClient();
        MockitoAnnotations.initMocks(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(m_client)
                .addConverterFactory(new GooglePlayDataFactory())
                .build();
        m_requestArgumentCaptor = ArgumentCaptor.forClass(Request.class);
        m_service = retrofit.create(GooglePlayService.class);
        when(m_client.newCall(any(Request.class))).thenAnswer(withString(""));
        reset(m_client);
    }

    @Test
    public void test_SearchWithoutNextToken_isFormatedWithoutToken() throws IOException {
        m_service.search("Test", "en", "us", null).execute();

        verify(m_client).newCall(m_requestArgumentCaptor.capture());

        assertThat(m_requestArgumentCaptor.getValue().httpUrl().toString(),
                is(String.format("%s/search?c=apps&q=Test&hl=en&lang=us", BASE_URL)));
    }

    @Test
    public void test_SearchWithNextToken_isFormatedWithToken() throws IOException {
        m_service.search("Test", "en", "us", "token").execute();

        verify(m_client).newCall(m_requestArgumentCaptor.capture());

        assertThat(m_requestArgumentCaptor.getValue().httpUrl().toString(),
                is(String.format("%s/search?c=apps&q=Test&hl=en&lang=us&pagTok=token", BASE_URL)));
    }



    private Answer<Call> withString(final String content) throws Throwable {
        return new Answer<Call>() {
            @Override
            public Call answer(InvocationOnMock invocation) throws Throwable {
                Call mock = mock(Call.class);
                ResponseBody body = ResponseBody.create(MediaType.parse("text/html"),
                        content);
                Response response = new Response.Builder()
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

    private Answer<Call> withFile(final String file) throws Throwable {
        return withString(fromFile(file));
    }

    private String fromFile(String file) throws URISyntaxException, IOException {
        java.net.URL url = GooglePlayServiceTest.class.getResource(file);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
    }
}
