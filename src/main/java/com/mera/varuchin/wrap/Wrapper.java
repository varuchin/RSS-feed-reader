package com.mera.varuchin.wrap;


import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import java.util.*;

public class Wrapper {

    private String title = null;
    private String name = null;
    private String item_link = null;
    private String feed_link = null;
    private String pub_date = null;
    private String description = null;
    private String word = null;


    public Wrapper() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem_link() {
        return item_link;
    }

    public void setItem_link(String item_link) {
        this.item_link = item_link;
    }

    public String getFeed_link() {
        return feed_link;
    }

    public void setFeed_link(String feed_link) {
        this.feed_link = feed_link;
    }

    public String getPub_date() {
        return pub_date;
    }

    public void setPub_date(String pub_date) {
        this.pub_date = pub_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }


    public void wrap(RssFeed rssFeed) {
        this.name = rssFeed.getName();
        this.feed_link = rssFeed.getLink().toString();
    }

    public void wrap(RssItem rssItem) {
        this.title = rssItem.getTitle();
        this.description = rssItem.getDescription();
        this.pub_date = rssItem.getPubDate().toString();
        this.item_link = rssItem.getLink().toString();
    }

    public List<Wrapper> wrapFeedList(List<RssFeed> rssFeedList) {
        List<Wrapper> result = new ArrayList<>();
        rssFeedList.stream().forEach(feed -> {
            Wrapper wrapper = new Wrapper();
            wrapper.wrap(feed);
            result.add(wrapper);
        });
        return result;
    }

    public List<Wrapper> wrapItemList(List<RssItem> rssItemList) {
        List<Wrapper> result = new ArrayList<>();
        rssItemList.stream().forEach(item -> {
            Wrapper wrapper = new Wrapper();
            wrapper.wrap(item);
            result.add(wrapper);
        });
        return result;
    }

    public List<Wrapper> topWords(Map<String, Integer> map) {
        List<Wrapper> wrappers = new ArrayList<>();
        Set<String> keys = map.keySet();
        List<String> wordList = new ArrayList<>();

        wordList.addAll(keys);
        wordList.stream().limit(5).forEach(key -> {
            Wrapper wrapper = new Wrapper();
            wrapper.setWord(key);
            wrappers.add(wrapper);
        });

        return wrappers;
    }
}
