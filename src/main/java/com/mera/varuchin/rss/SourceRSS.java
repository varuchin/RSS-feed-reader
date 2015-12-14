package com.mera.varuchin.rss;


import java.net.URL;

public class SourceRSS {

    private RssItem rssItem;
    private String title;
    private URL link;

    public SourceRSS(RssItem rssItem){
        this.rssItem = rssItem;
        this.title = rssItem.getTitle();
        this.link = rssItem.getLink();
    }
}
