package com.mera.varuchin.dao;

import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import java.net.URL;
import java.util.List;
import java.util.Map;


public interface RssItemDAO {

    void add(RssItem rssItem);

    void add(URL link, RssFeed rssFeed);

    void remove(URL link);

    void remove(RssItem rssItem);

    List<RssItem> getByLink(URL link);

    List<RssItem> getAllItems();

    List<RssItem> getAllItemsWithId(Long feed_id);

    Map<String, Integer> getTopWords(Long item_id);

    Map<String, URL> getAllSourcesRss();

    RssItem getById(Long id);


}
