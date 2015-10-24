package de.halfreal.googleplayscraper.service;

import de.halfreal.googleplayscraper.model.App;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

public interface GooglePlayService {


    @GET("/search?c=apps")
    Call<List<App>> search(@Query("q") String query, @Query("hl") String language, @Query("lang") String country, @Query("pagTok") String nextToken);

}
