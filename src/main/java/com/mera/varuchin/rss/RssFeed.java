package com.mera.varuchin.rss;


import javax.persistence.*;
import java.net.URL;

@Entity
@Table(name = "RSSfeed")
public class RssFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @OneToOne(mappedBy = "RSS")
//    @JoinTable(name = "RSS")
//    @JoinColumn(name = "FEED_ID")

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "RSSfeed", cascade = CascadeType.REMOVE)
//    private Set<RssItem> items = new HashSet<>();


    @Column(name = "ID", nullable = false)
    private Long id;

//    @Column
//    private Long item_id;

    @Column(nullable = false)
    private String name;

    @Column
    private URL link;

    public RssFeed() {
    }

    public RssFeed(String name, URL link) {
        this.name = name.toUpperCase();
        this.link = link;
    }
//    public RssFeed(Long item_id, String name, URL link) {
//        this.item_id = item_id;
//        this.name = name.toUpperCase();
//        this.link = link;
//    }

    public RssFeed(RssItem rssItem, String name) {
        this.name = name.toUpperCase();
        this.link = rssItem.getLink();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public Long getItem_id() {
//        return item_id;
//    }
//
//    public void setItem_id(Long item_id) {
//        this.item_id = item_id;
//    }

    public String getName() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }
}
