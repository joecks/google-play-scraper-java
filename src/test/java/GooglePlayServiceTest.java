import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;

import de.halfreal.googleplayscraper.model.GooglePlayDataFactory;
import retrofit.Retrofit;

import static org.mockito.Mockito.verify;

public class GooglePlayServiceTest {

    public static final String BASE_URL = "http://test.com";

    private GooglePlayService m_service;
    @Spy
    private OkHttpClient m_client;
    private ArgumentCaptor<Request> m_requestArgumentCaptor;

    @Before
    public void setUp() {
        m_client = new OkHttpClient();
        MockitoAnnotations.initMocks(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(m_client)
                .addConverterFactory(new GooglePlayDataFactory())
                .build();
        m_requestArgumentCaptor = ArgumentCaptor.forClass(Request.class);
        m_service = retrofit.create(GooglePlayService.class);
    }

    @Test
    public void test_SearchWithoutNextToken_isFormatedWithoutToken() throws IOException {
        m_service.search("Test", "en", "us", null).execute();

        verify(m_client).newCall(m_requestArgumentCaptor.capture());

        Assert.assertThat(m_requestArgumentCaptor.getValue().httpUrl().toString(),
                Is.is(String.format("%s/search?c=apps&q=Test&hl=en&lang=us", BASE_URL)));
    }

    @Test
    public void test_SearchWithNextToken_isFormatedWithToken() throws IOException {
        m_service.search("Test", "en", "us", "token").execute();

        verify(m_client).newCall(m_requestArgumentCaptor.capture());

        Assert.assertThat(m_requestArgumentCaptor.getValue().httpUrl().toString(),
                Is.is(String.format("%s/search?c=apps&q=Test&hl=en&lang=us&pagTok=token", BASE_URL)));
    }
}
