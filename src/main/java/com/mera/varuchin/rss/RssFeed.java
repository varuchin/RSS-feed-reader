package com.mera.varuchin.rss;


import javax.persistence.*;
import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RssFeed")
public class RssFeed {


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rssFeed", cascade = CascadeType.ALL)
    private Set<RssItem> items = new HashSet<>();

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String name;

    @Column
    private URL link;

    public static final transient LocalTime creationTime =
            LocalTime.now(ZoneId.of("Europe/Berlin"));

    public RssFeed() {
    }

    public RssFeed(String name, URL link) {
        this.name = name.toUpperCase();
        this.link = link;
    }

    public Set<RssItem> getItems() {
        return items;
    }

    public void setItems(Set<RssItem> items) {
        this.items = items;
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
        this.name = name;
    }

    public URL getLink() {
        return link;
    }

    public  void addItem(RssItem rssItem){
        this.items.add(rssItem);
    }
    public void setLink(URL link) {
        this.link = link;
    }

    public static LocalTime getCreationTime() {
        return creationTime;
    }

}
