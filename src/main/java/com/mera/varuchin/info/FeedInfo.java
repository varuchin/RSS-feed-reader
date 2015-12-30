package com.mera.varuchin.info;


import com.mera.varuchin.rss.RssFeed;

import java.util.ArrayList;
import java.util.List;

public class FeedInfo {

    private String name = null;
    private String feed_link = null;
    private RssFeed rssFeed;

    public FeedInfo(){}

    public FeedInfo(RssFeed rssFeed){
        this.name = rssFeed.getName();
        this.feed_link = rssFeed.getLink().toString();
        this.rssFeed = rssFeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeed_link() {
        return feed_link;
    }

    public void setFeed_link(String feed_link) {
        this.feed_link = feed_link;
    }

    public RssFeed getRssFeed() {
        return rssFeed;
    }

    public void setRssFeed(RssFeed rssFeed) {
        this.rssFeed = rssFeed;
    }

    public void setInfo(RssFeed rssFeed) {
        this.name = rssFeed.getName();
        this.feed_link = rssFeed.getLink().toString();
        this.rssFeed = rssFeed;
    }

    public List<FeedInfo> setFeedListInfo(List<RssFeed> rssFeedList) {
        List<FeedInfo> result = new ArrayList<>();
        rssFeedList.stream().forEach(feed -> {
            FeedInfo feedInfo = new FeedInfo();
            feedInfo.setInfo(feed);
            result.add(feedInfo);
            this.rssFeed = feed;
        });

        return result;
    }
}
