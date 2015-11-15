package de.halfreal.googleplayscraper.model;

import java.util.List;

/**
 * Created by joecks on 26.10.15.
 */
public class AppResponse {

    private final List<App> m_apps;
    private final String m_token;

    public AppResponse(List<App> apps, String token) {
        m_apps = apps;
        m_token = token;
    }

    public List<App> getApps() {
        return m_apps;
    }

    public String getToken() {
        return m_token;
    }
}
