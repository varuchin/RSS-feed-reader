package com.mera.varuchin.rss;


import java.net.URL;

@Deprecated
public class SourceRSS {

    private String title;
    private URL link;

    public SourceRSS(RssItem rssItem){
        this.title = rssItem.getTitle();
        this.link = rssItem.getLink();
    }

    public SourceRSS(String title, URL link){
        this.title = title;
        this.link = link;
    }
}
