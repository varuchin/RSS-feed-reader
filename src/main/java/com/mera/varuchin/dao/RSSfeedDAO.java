package com.mera.varuchin.dao;


import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import java.io.ObjectInputStream;
import java.net.URL;
import java.util.List;

public interface RSSfeedDAO {

    void add(RssFeed rssFeed);

    void update(RssFeed rssfeed);

    void remove(Long id);

    void parseSources(ObjectInputStream inputStream);

    void refresh(RssFeed rssFeed);

    RssFeed getById(Long id);

    RssFeed getByLink(URL link);

    RssItem getBySource(Long feed_id, Long item_id);

    List<RssFeed> getFeedsByName(int page, int pageSize, String name);

    List<RssItem> getNewsFromSource(URL source);

    List<RssFeed> getAllRegisteredFeeds();
}
