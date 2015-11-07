package de.halfreal.googleplayscraper.model;

import rx.Observable;

/**
 * Created by joecks on 26.10.15.
 */
public class Response {

    private final Observable<App> m_apps;
    private final String m_token;

    public Response(Observable<App> apps, String token) {
        m_apps = apps;
        m_token = token;
    }

    public Observable<App> getApps() {
        return m_apps;
    }

    public String getToken() {
        return m_token;
    }
}
