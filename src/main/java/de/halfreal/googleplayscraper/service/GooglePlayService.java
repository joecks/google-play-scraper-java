package de.halfreal.googleplayscraper.service;

import javax.validation.constraints.NotNull;

import de.halfreal.googleplayscraper.model.AppResponse;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface GooglePlayService {

    @NotNull
    String BASE_URL = "https://play.google.com";

    @GET("/store/search?c=apps")
    Observable<AppResponse> search(@Query("q") String query, @Query("hl") String language, @Query("gl") String country, @Query("pagTok") String nextToken);
}
