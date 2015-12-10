package com.mera.varuchin.dao;

import com.mera.varuchin.rss.RssItem;

import java.net.URL;
import java.util.Collection;


public interface RssItemDAO {

    void add(RssItem rssItem);
    void remove(Long id);
    void update(RssItem rssItem);
    void update(RssItem rssItem, String name);
    void update(RssItem rssItem, URL url);
    void update(RssItem rssItem, String name, URL url);
    RssItem getById(long id);
    RssItem getByRssSource(RssItem rssItem);
    Collection<RssItem> getAllRss();
    Collection<String> getTopWords(RssItem rssItem);
    Collection<RssItem> getPaginatedList();
}
