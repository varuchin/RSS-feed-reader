package com.mera.varuchin.dao;

import com.mera.varuchin.rss.RssItem;

import java.net.URL;
import java.util.Collection;
import java.util.Map;


public interface RssItemDAO {

    void add(RssItem rssItem);

    void add(URL link);

    void remove(URL link);

    Collection<RssItem> getByLink(URL link);

    Collection<RssItem> getAllItems();

    Map<String, Integer> getTopWords(RssItem rssItem);

    Map<String, URL> getAllSourcesRss();

    RssItem getBySource(String title, URL link);

    RssItem getByTitle(String title);

    Collection<RssItem> getItemsWithLink(URL link);

}
