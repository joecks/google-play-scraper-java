package de.halfreal.googleplayscraper.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.halfreal.googleplayscraper.model.App;
import de.halfreal.googleplayscraper.model.AppResponse;
import de.halfreal.googleplayscraper.service.GooglePlayService;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GooglePlayApiTest {

    private GooglePlayApi m_api;
    @Mock
    private GooglePlayService m_service;
    private Subscription m_subscribe;

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        m_api = new GooglePlayApi(m_service);
        when(m_service.search(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Observable.just(new AppResponse(null, null)));
    }

    @Test
    public void text_GivenTwoPageCallesAndNoResulty_ClientIsCalledOnce() {
        final Observable<List<App>> search = m_api.search("Clipboard Action", "en", "us", 2);

        search.toBlocking().singleOrDefault(null);

        verify(m_service, times(1))
                .search(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void text_GivenTwoPageCalles_ClientIsCalledTwice() throws Throwable {
        when(m_service.search(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Observable.just(new AppResponse(null, "Token")));
        final Observable<List<App>> search = m_api.search("Clipboard Action", "en", "us", 2);

        search.toBlocking().firstOrDefault(null);

        verify(m_service, times(2))
                .search(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void text_GivenTwoPageCallesUnsubscribeAfterFirst_ClientIsCalledOnce() throws Throwable {
        ArrayList<App> apps = new ArrayList<App>();
        apps.add(new App("", "", "", "", "", 0f, "", false));
        when(m_service.search(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Observable.just(new AppResponse(apps, "Token")));
        final Observable<List<App>> search = m_api.search("Clipboard Action", "en", "us", 2);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final PublishSubject<Void> stop  = PublishSubject.create();
        search.takeUntil(stop).subscribe(new Subscriber<List<App>>() {
            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(List<App> apps) {
                stop.onNext(null);
            }
        });
        countDownLatch.await(5, TimeUnit.SECONDS);

        verify(m_service, times(1))
                .search(anyString(), anyString(), anyString(), anyString());
    }


    @Test
    public void text_GivenNoPageCalles_ClientIsNotCalled() {
        final Observable<List<App>> search = m_api.search("Clipboard Action", "en", "us", 0);

        search.toBlocking().singleOrDefault(null);

        verify(m_service, never())
                .search(anyString(), anyString(), anyString(), anyString());
    }

    @Test(expected = RuntimeException.class)
    public void text_GivenRuntimeErrors_ErrorsArePropagatedToConsumer() {
        when(m_service.search(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Observable.<AppResponse>error(new RuntimeException("")));
        final Observable<List<App>> search = m_api.search("Clipboard Action", "en", "us", 2);

        search.toBlocking().firstOrDefault(null);
    }
}
