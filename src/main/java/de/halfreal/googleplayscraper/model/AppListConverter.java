package de.halfreal.googleplayscraper.model;

import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import retrofit.Converter;
import rx.Observable;
import rx.functions.Func1;

public class AppListConverter implements Converter<ResponseBody, AppResponse> {

    public AppListConverter(@NotNull String baseUrl) {
        m_baseUrl = baseUrl;
    }

    @NotNull
    private final String m_baseUrl;

    @Override
    public AppResponse convert(ResponseBody value) throws IOException {
        Document doc = Jsoup.parse(value.byteStream(), "UTF-8", m_baseUrl);
        Matcher matcher = Pattern.compile("\\\\42(GAE.+?)\\\\42").matcher(doc.html());
        String token =null ;
        if (matcher.find()) {
            token = matcher.group(1);
        }
        //return s[1].replace(/\\\\u003d/g, '=');
        if (token != null) {
            token = token.replace("\\\\u003d", "=");
        }
        final List<App> apps = Observable.from(doc.select(".card"))
                .map(new Func1<Element, App>() {
                    @Override
                    public App call(Element element) {
                        return parseApp(element);
                    }
                }).toList().toBlocking().first();
        return new AppResponse(apps, token);
    }

    @NotNull
    private App parseApp(Element element) {
        Float score = null;
        String price = null;
        String scoreText = element.select("div.tiny-star").attr("aria-label");
        if (scoreText != null) {
            Matcher matcher = Pattern.compile("([\\d.]+)").matcher(scoreText);
            if (matcher.find()){
                score = Float.parseFloat(matcher.group());
            }
        }

        Element priceElement = element.select("span.display-price").first();
        if (priceElement != null) {
            price = priceElement.text();
        }
        boolean free = price == null || !price.matches("\\d");

        Element a = element.select("a").first();
        Element aTitle = element.select("a.title").first();
        Element aSubtitle = element.select("a.subtitle").first();
        Element aCoverImage = element.select("img.cover-image").first();

        String href = a != null ? m_baseUrl + a.attr("href") : null;
        String title = aTitle != null ? aTitle.attr("title") : null;
        String developer = aSubtitle != null ? aSubtitle.text() : null;
        String icon = aCoverImage != null ? aCoverImage.attr("data-cover-large") : null;
        return new App(
                href,
                element.attr("data-docId"),
                title,
                developer,
                icon,
                score,
                price,
                free);
    }
}
