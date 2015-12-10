package com.mera.varuchin.rss;


import javax.persistence.*;
import java.net.URL;
import java.util.Date;

@Entity
@Table(name = "RSS")
public class RssItem {

    /**
     * –¿«Œ¡–¿“‹—ﬂ  ¿  √≈Õ≈–»“‹ ID
     */
    @Id
    //@GeneratedValue(strategy=SEQUENCE, generator="RSS_SEQ")
    @Column(name="ID")
    private Long id;

    @Column(name="NAME")
    private String name = null;

    @Column(name="TITLE")
    private String title= null;

    @Column(name="DESCRIPTION")
    private String description= null;

    @Column(name="PUB_DATE")
    private Date pubDate= null;

    @Column(name="link")
    private URL link = null;

    public RssItem(){}

    public RssItem(Long id, String name, String title, String description, Date pubDate, URL link) {
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
