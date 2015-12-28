package com.mera.varuchin.dao;


import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import java.net.URL;
import java.util.List;

public interface RssFeedDAO {

    void add(RssFeed rssFeed);

    void update(RssFeed rssfeed);

    void remove(Long id);

    void refresh(RssFeed rssFeed);

    RssFeed getById(Long id);

    RssFeed getByLink(URL link);

    RssItem getBySource(Long feed_id, Long item_id);

    List<RssFeed> getFeeds(Integer page, Integer pageSize, String name);

}
