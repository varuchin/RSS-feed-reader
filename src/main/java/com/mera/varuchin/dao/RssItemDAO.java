package com.mera.varuchin.dao;

import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import java.util.List;
import java.util.Map;


public interface RssItemDAO {

    void addItems(RssFeed rssFeed);

    List<RssItem> getItems(Integer page, Integer pageSize);

    Map<String, Integer> getTopWords(Long item_id);

    RssItem getById(Long id);

}
