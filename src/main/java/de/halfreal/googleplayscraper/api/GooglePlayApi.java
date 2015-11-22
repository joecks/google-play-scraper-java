package de.halfreal.googleplayscraper.api;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.constraints.NotNull;

import de.halfreal.googleplayscraper.model.App;
import de.halfreal.googleplayscraper.model.AppResponse;
import de.halfreal.googleplayscraper.model.GooglePlayDataFactory;
import de.halfreal.googleplayscraper.service.GooglePlayService;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class GooglePlayApi {

    @NotNull
    private final GooglePlayService m_service;

    public GooglePlayApi() {
        this(new Retrofit.Builder()
                .baseUrl(GooglePlayService.BASE_URL)
                .addConverterFactory(new GooglePlayDataFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(GooglePlayService.class));
    }

    GooglePlayApi(@NotNull GooglePlayService service) {
        m_service = service;
    }

    public Observable<List<App>> search(@NotNull final String query, @NotNull final String language,
                                        @NotNull final String country, final int maxPages) {
        final AtomicInteger page = new AtomicInteger(0);

        return Observable.create(new Observable.OnSubscribe<List<App>>() {
            @Override
            public void call(final Subscriber<? super List<App>> subscriber) {
                String token = null;
                if (maxPages > 0) {
                    do {
                        final Observable<AppResponse> search = m_service.search(query, language, country, token);
                        final AppResponse response = search
                                .doOnError(new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        subscriber.onError(throwable);
                                    }
                                })
                                .toBlocking()
                                .firstOrDefault(null);
                        if (response != null) {
                            token = response.getToken();
                            subscriber.onNext(response.getApps());
                        }
                    } while (page.incrementAndGet() < maxPages && token != null);
                }
                subscriber.onCompleted();
            }
        }).filter(new Func1<List<App>, Boolean>() {
            @Override
            public Boolean call(List<App> apps) {
                return apps != null && !apps.isEmpty();
            }
        });
    }

}
