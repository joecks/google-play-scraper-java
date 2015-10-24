package de.halfreal.googleplayscraper.service;

import javax.validation.constraints.NotNull;

import de.halfreal.googleplayscraper.model.App;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface GooglePlayService {

    @NotNull
    String BASE_URL = "https://play.google.com";

    @GET("/store/search?c=apps")
    Call<Observable<App>> search(@Query("q") String query, @Query("hl") String language, @Query("lang") String country, @Query("pagTok") String nextToken);

}
