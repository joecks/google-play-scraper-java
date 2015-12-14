package de.halfreal.googleplayscraper.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.halfreal.googleplayscraper.api.GooglePlayApi;
import de.halfreal.googleplayscraper.api.HumanRequestBehavior;
import de.halfreal.googleplayscraper.model.App;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;


public class GooglePlayServiceEndToEndTest {

    public static final Func1<List<App>, Observable<App>> MAP_TO_OBSERVABLE = new Func1<List<App>, Observable<App>>() {
        @Override
        public Observable<App> call(List<App> apps) {
            return Observable.from(apps);
        }
    };
    private volatile Throwable m_exception;

    @Test
    public void text_SingleCallsToGooglePlay_WillPassWithout503() {
        final GooglePlayApi googlePlayApi = new GooglePlayApi(new HumanRequestBehavior());

        final rx.Observable<List<App>> search = googlePlayApi.search("Clipboard Action", "en", "us", 1);
        List<App> apps = search
                .flatMap(MAP_TO_OBSERVABLE)
                .toList()
                .toBlocking()
                .first();
        Assert.assertThat(apps.size(), is(20));
    }

    @Test
    public void text_MaxCallsToGooglePlay_WillPassWithout503() throws InterruptedException {
        final GooglePlayApi googlePlayApi = new GooglePlayApi(new HumanRequestBehavior(3000, 3000));

        final int parallelExecutions = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(parallelExecutions);
        for (int i = 0; i < parallelExecutions; i++) {
            final int count = i;
            System.out.println("Thread started #" + count);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Observable<List<App>> search = googlePlayApi.search("Game " + (int)(Math.random()*1000), "en", "us", 7);
                    search
                            .flatMap(MAP_TO_OBSERVABLE)
                            .toList()
                            .subscribe(new Subscriber<List<App>>() {
                                @Override
                                public void onCompleted() {
                                    countDownLatch.countDown();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    m_exception = e;
                                    for (int i = 0; i < parallelExecutions; i++) {
                                        countDownLatch.countDown();
                                    }
                                }

                                @Override
                                public void onNext(List<App> apps) {
                                    System.out.println("Thread completed #" + count + " Results: " + apps.size());
                                }
                            });
                }
            }).start();
        }

        countDownLatch.await(5, TimeUnit.MINUTES);
        Assert.assertThat("Received an error, while benchmarking google.", m_exception, is(nullValue()));
    }
}
