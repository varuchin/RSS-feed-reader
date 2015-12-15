package com.mera.varuchin.rss;


import javax.persistence.*;
import java.net.URL;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "RSS")
public class RssItem {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 35)
    private String name = null;

    @Column(name = "TITLE", length = 80, nullable = false)
    private String title = null;

    @Column(name = "DESCRIPTION", length = 150)
    private String description = null;

    @Column(name = "PUB_DATE")
    @Temporal(value = TemporalType.DATE)
    private Date pubDate = null;

    @Column(name = "LINK", length = 150, nullable = false)
    private URL link = null;

    private transient SourceRSS sourceRSS = new SourceRSS(this.title, this.link);

    private transient Collection<String> topWords = null;

    public RssItem() {
    }


    public RssItem(String name, String title, String description, Date pubDate, URL link) {
        this.name = name.toUpperCase();
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.link = link;
        this.sourceRSS = new SourceRSS(title, link);
    }

    public RssItem(Long id, String name, String title, String description, Date pubDate, URL link) {
        this.id = id;
        this.name = name.toUpperCase();
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.link = link;
        this.sourceRSS = new SourceRSS(title, link);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
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

    public void setSourceRss(SourceRSS sourceRSS) {
        this.sourceRSS = sourceRSS;
    }

    public SourceRSS getSourceRSS() {
        return sourceRSS;
    }


    @Override
    public String toString() {
        return "RssItem{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pubDate=" + pubDate +
                ", link='" + link + '\'' +
                '}';
    }
}
