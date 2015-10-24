package de.halfreal.googleplayscraper.model;

import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import de.halfreal.googleplayscraper.service.GooglePlayService;
import retrofit.Converter;

public class GooglePlayDataFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        return new AppListConverter(GooglePlayService.BASE_URL);
    }
}
