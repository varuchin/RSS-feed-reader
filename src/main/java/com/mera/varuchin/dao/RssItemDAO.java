package com.mera.varuchin.dao;

import com.mera.varuchin.pagination.Pageable;
import com.mera.varuchin.rss.RssItem;

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

    RssItem getById(Long id);

    RssItem getByLink(URL link);

    Collection<RssItem> getAllRss();

    Collection<RssItem> getRssSortedByName(String name);

    Map<String, Integer> getTopWords(RssItem rssItem);

    Map<String, URL> getAllSourcesRss();

    RssItem getBySource(String title, URL link);

    Pageable<RssItem> getPaginatedListFiltered(String name);
}
