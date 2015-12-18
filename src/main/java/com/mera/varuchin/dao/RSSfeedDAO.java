package com.mera.varuchin.dao;


import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public interface RSSfeedDAO {

    void add(RssFeed rssFeed);

   // void add(URL feedURL, String name);

    void update(RssFeed rssfeed);

    void update(RssFeed rssFeed, String name);

    void update(RssFeed rssFeed, URL link);

    void update(RssFeed rssFeed, String name, URL link);

    void remove(Long id);

    RssFeed getById(Long id);

    RssFeed getByLink(URL link);

    RssItem getBySource(String title, URL link);

    ArrayList<RssFeed> getAllListed(int start, int limit);

    Collection<RssFeed> getRssSortedByName(String name);

    Collection<RssFeed> getFeedsByName(int page, int pageSize, String name);

    Collection<RssItem> getNewsFromSource(URL source);

    Collection<RssFeed> getAllRegisteredFeeds();

    Collection<RssItem> getAllItems(Long id);




}
