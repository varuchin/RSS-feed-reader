package com.mera.varuchin.dao;

import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import java.util.List;
import java.util.Map;


public interface RssItemDAO {

    void add(RssFeed rssFeed);

    void remove(RssItem rssItem);

    List<RssItem> getItems(Integer page, Integer pageSize);

    List<RssItem> getAllItemsWithId(Long feed_id);

    Map<String, Integer> getTopWords(Long item_id);

    RssItem getById(Long id);


}
