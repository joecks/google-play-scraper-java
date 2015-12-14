package de.halfreal.googleplayscraper.api;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
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
    private final RequestBehavior m_requestBehavior;

    public GooglePlayApi() {
        this(null);
    }

    public GooglePlayApi(@NotNull RequestBehavior requestBehavior) {
        this(new Retrofit.Builder()
                .client(createClient(requestBehavior))
                .baseUrl(GooglePlayService.BASE_URL)
                .addConverterFactory(new GooglePlayDataFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(GooglePlayService.class), requestBehavior);
    }

    private static OkHttpClient createClient(final RequestBehavior behavior) {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain
                        .request()
                        .newBuilder()
                        .addHeader("user-agent", behavior.userAgent())
                        .build();
                return chain.proceed(request);
            }
        });
        return client;
    }

    GooglePlayApi(@NotNull GooglePlayService service, RequestBehavior requestBehavior) {
        if (requestBehavior == null) {
            requestBehavior = new RequestBehavior();
        }
        m_requestBehavior = requestBehavior;
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
                        try {
                            m_requestBehavior.awaitContinue();
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
                        } catch (InterruptedException e) {
                            subscriber.onError(e);
                        }
                    } while (page.incrementAndGet() < maxPages
                            && token != null
                            && !subscriber.isUnsubscribed());
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
