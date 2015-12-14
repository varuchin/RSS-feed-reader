package com.mera.varuchin.dao;

import com.mera.varuchin.rss.RssItem;
import com.mera.varuchin.rss.SourceRSS;

import java.net.URL;
import java.util.Collection;
import java.util.Map;


public interface RssItemDAO {

    void add(RssItem rssItem);
    void add(URL url, String name);
    void remove(Long id);
    void update(RssItem rssItem);
    void update(RssItem rssItem, String name);
    void update(RssItem rssItem, URL url);
    void update(RssItem rssItem, String name, URL url);
    RssItem getById(long id);
    SourceRSS getByRssSource(RssItem rssItem);
    Collection<RssItem> getAllRss();
    Map<String, Integer> getTopWords(RssItem rssItem);
    Collection<RssItem> getPaginatedList();
}
