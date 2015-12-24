package com.mera.varuchin.info;


import com.mera.varuchin.rss.RssItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemInfo {

    private String title = null;
    private String item_link = null;
    private String feed_link = null;
    private String pub_date = null;
    private String description = null;
    private String word = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItem_link() {
        return item_link;
    }

    public void setItem_link(String item_link) {
        this.item_link = item_link;
    }

    public String getFeed_link() {
        return feed_link;
    }

    public void setFeed_link(String feed_link) {
        this.feed_link = feed_link;
    }

    public String getPub_date() {
        return pub_date;
    }

    public void setPub_date(String pub_date) {
        this.pub_date = pub_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setInfo(RssItem rssItem) {
        this.title = rssItem.getTitle();
        this.description = rssItem.getDescription();
        this.pub_date = rssItem.getPubDate();
        this.item_link = rssItem.getLink().toString();
    }

    public List<ItemInfo> setItemListInfo(List<RssItem> rssItemList) {
        List<ItemInfo> result = new ArrayList<>();
        rssItemList.stream().forEach(item -> {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setInfo(item);
            result.add(itemInfo);
        });
        return result;
    }

    public List<ItemInfo> topWords(Map<String, Integer> map) {
        List<ItemInfo> infos = new ArrayList<>();
        Set<String> keys = map.keySet();
        List<String> wordList = new ArrayList<>();

        wordList.addAll(keys);
        wordList.stream().limit(5).forEach(key -> {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setWord(key);
            infos.add(itemInfo);
        });

        return infos;
    }
}
