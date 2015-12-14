# Google Play Scraper - Java 

This project is inspired by the really cool  [google-play-scraper](https://github.com/facundoolano/google-play-scraper) node projejct.
 
It is purly written in Java and uses **JavaRx** and **Retrofit**.



## Usage

### Gradle
Add it as dependency. Fist add the repository and then the compile dependeny:

```gradle
repositories {
    mavenCentral()
    maven { url "https://raw.githubusercontent.com/joecks/google-play-scraper-java/master/release/" }
}
```

```gradle
compile 'de.halfreal.googleplayscraper:googleplayscraper-java:1.0.2'
```

### Getting started

```java
GooglePlayApi api = new GooglePlayApi();

api.search("Clipboard Actions", "en", "us", 3)
   .onNext(/** Do something  on the next list**/);

```

### search

Scraps google play for 60 search results in 3 queries:

```java
Observable<List<App>> list = search("Clipboard Actions", "en", "us", 3);
```
