# Google Play Scraper - Java 

This project is inspired by the really cool  [google-play-scraper](https://github.com/facundoolano/google-play-scraper) node projejct.
 
It is purly written in Java and uses **JavaRx** and **Retrofit**.



## Usage
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