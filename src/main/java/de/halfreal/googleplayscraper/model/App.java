package de.halfreal.googleplayscraper.model;

public class App {


    public final String m_url;
    public final String m_appId;
    public final String m_title;
    public final String m_developer;
    public final String m_icon;
    public final Float m_score;
    public final String m_price;
    public final boolean m_free;

    public App(String url, String appId, String title, String developer, String icon, Float score, String price, boolean free) {
        m_url = url;
        m_appId = appId;
        m_title = title;
        m_developer = developer;
        m_icon = icon;
        m_score = score;
        m_price = price;
        m_free = free;
    }

    public String getUrl() {
        return m_url;
    }

    public String getAppId() {
        return m_appId;
    }

    public String getTitle() {
        return m_title;
    }

    public String getDeveloper() {
        return m_developer;
    }

    public String getIcon() {
        return m_icon;
    }

    public Float getScore() {
        return m_score;
    }

    public String getPrice() {
        return m_price;
    }

    public boolean isFree() {
        return m_free;
    }
}
