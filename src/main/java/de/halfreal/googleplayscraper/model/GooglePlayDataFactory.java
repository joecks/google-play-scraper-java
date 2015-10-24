package de.halfreal.googleplayscraper.model;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

public class GooglePlayDataFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        return new Converter<ResponseBody, App>() {
            @Override
            public App convert(ResponseBody value) throws IOException {
                return new App();
            }
        };
    }
}
