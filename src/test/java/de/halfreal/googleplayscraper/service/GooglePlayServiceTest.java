package de.halfreal.googleplayscraper.service;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import de.halfreal.googleplayscraper.TestHelper;
import de.halfreal.googleplayscraper.api.GooglePlayApi;
import de.halfreal.googleplayscraper.model.App;
import de.halfreal.googleplayscraper.model.AppResponse;
import de.halfreal.googleplayscraper.model.GooglePlayDataFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

import static de.halfreal.googleplayscraper.TestHelper.withFile;
import static de.halfreal.googleplayscraper.TestHelper.withString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
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
        m_service.search("Test", "en", "us", null).toBlocking().single();

        verify(m_client).newCall(m_requestArgumentCaptor.capture());

        assertThat(m_requestArgumentCaptor.getValue().httpUrl().toString(),
                is(String.format("%s/store/search?c=apps&q=Test&hl=en&gl=us", BASE_URL)));
    }

    @Test
    public void test_SearchWithNextToken_isFormatedWithToken() throws IOException {
        m_service.search("Test", "en", "us", "token").toBlocking().single();

        verify(m_client).newCall(m_requestArgumentCaptor.capture());

        assertThat(m_requestArgumentCaptor.getValue().httpUrl().toString(),
                is(String.format("%s/store/search?c=apps&q=Test&hl=en&gl=us&pagTok=token", BASE_URL)));
    }

    @Test
    public void test_SearchWithResults_HasCartElements() throws Throwable {
        when(m_client.newCall(any(Request.class)))
                .thenAnswer(withFile("search-response.html"));

        final AppResponse response = m_service.search("Test", "en", "us", null).toBlocking().single();

        assertThat(response.getApps().size(), is(20));
    }

    @Test
    public void test_SearchFirstElement_ContainsAppIdTitleDeveloperIconScorePriceAndIfItisFree() throws Throwable {
        when(m_client.newCall(any(Request.class)))
                .thenAnswer(withFile("search-response.html"));

        AppResponse response = m_service.search("Test", "en", "us", null).toBlocking().first();

        App app = response.getApps().get(0);
        assertThat(app.getAppId(), is("com.lukaville.mental.age"));
        assertThat(app.getTitle(), is("Mental Age Test"));
        assertThat(app.getDeveloper(), is("Dainty Apps"));
        assertThat(app.getIcon(), is("https://lh5.ggpht.com/Cron7ycndvE5QUT9CHx72cIDqchqCUca-mtQSYv6_18HDwkCWgNBQueZ51uUMdUuzQ=w340"));
        assertThat(app.getScore(), is(3.2f));
        assertThat(app.getPrice(), is(nullValue()));
        assertThat(app.getUrl(), is("https://play.google.com/store/apps/details?id=com.lukaville.mental.age"));
        assertThat(app.isFree(), is(true));
    }


//    @Test
//    public void textX() {
//        final GooglePlayApi googlePlayApi = new GooglePlayApi();
//
//        final Observable<List<App>> search = googlePlayApi.search("Clipboard Action", "en", "us", 2);
//
//        final List<App> apps = search.flatMap(new Func1<List<App>, Observable<App>>() {
//            @Override
//            public Observable<App> call(List<App> apps) {
//                return Observable.from(apps);
//            }
//        }).toList().toBlocking().single();
//
//        Assert.assertTrue(apps.size() + " , 20", apps.size() == 20);
//    }
}
