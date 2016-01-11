package com.mera.varuchin.rss;


import com.mera.varuchin.dao.RssItemDAOImpl;

import javax.persistence.*;
import java.net.URL;
import java.util.Map;

@Entity
@Table(name = "RssItem")
public class RssItem {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "feed_id", referencedColumnName = "id")
    private RssFeed rssFeed;

    @Column(name = "TITLE", length = 1000, nullable = false)
    private String title = null;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description = null;

    @Column(name = "PUB_DATE")
    private String pubDate = null;

    @Column(name = "LINK", length = 1000, nullable = false)
    private URL link = null;


//    private transient Map<String, Integer> topWords = getTopWords();

    public RssItem() {
    }

    public RssItem(String title, String description, String pubDate, URL link) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public RssFeed getRssFeed() {
        return rssFeed;
    }

    public void setRssFeed(RssFeed rssFeed) {
        this.rssFeed = rssFeed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public Map<String, Integer> getTopWords() {
        RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
        return rssItemDAO.getTopWords(this.id);
    }


    @Override
    public String toString() {
        return "RssItem{" +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pubDate=" + pubDate +
                ", link='" + link + '\'' +
                '}';
    }
}
