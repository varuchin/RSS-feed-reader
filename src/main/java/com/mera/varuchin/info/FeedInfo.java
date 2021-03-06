package com.mera.varuchin.info;


import com.mera.varuchin.rss.RssFeed;

import java.util.ArrayList;
import java.util.List;

public class FeedInfo {

    private String name = null;
    private String feed_link = null;

    public FeedInfo(){}


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

    public void setInfo(RssFeed rssFeed) {
        this.name = rssFeed.getName();
        this.feed_link = rssFeed.getLink().toString();
    }

    public void setInfo(String name, String feed_link){
        this.name = name;
        this.feed_link = feed_link;
    }

    public List<FeedInfo> setFeedListInfo(List<RssFeed> rssFeedList) {
        List<FeedInfo> result = new ArrayList<>();
        rssFeedList.stream().forEach(feed -> {
            FeedInfo feedInfo = new FeedInfo();
            feedInfo.setInfo(feed);
            result.add(feedInfo);
        });

        return result;
    }
}
