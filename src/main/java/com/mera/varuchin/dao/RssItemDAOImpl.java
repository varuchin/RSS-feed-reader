package com.mera.varuchin.dao;

import com.mera.varuchin.SessionProvider;
import com.mera.varuchin.parsers.ItemParser;
import com.mera.varuchin.rss.RssExecutor;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.apache.http.HttpEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class RssItemDAOImpl implements RssItemDAO {

    private static SessionProvider sessionProvider = new SessionProvider();

    public RssItemDAOImpl() {
    }

    @Override
    public void addItems(RssFeed rssFeed) {
        RssExecutor rssExecutor = new RssExecutor();

        Runnable task = () -> {
            HttpEntity httpEntity = null;
            try {

                httpEntity = RssFeedDAOImpl.getEntityFromFeed(rssFeed);
                if (httpEntity != null) {
                    ItemParser parser = new ItemParser();
                    InputStream inputStream = httpEntity.getContent();
                    List<RssItem> items = parser.parseItems(inputStream);
                    items.forEach(item -> {
                        item.setRssFeed(rssFeed);
                        rssFeed.addItem(item);
                        add(item);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        rssExecutor.run(task);
    }

    @Override
    public List<RssItem> getItems(Integer page, Integer pageSize) {
        List<RssItem> items;

        try (Session session = getSession()) {
            if (page != null && pageSize != null) {
                Criteria criteria = session.createCriteria(RssItem.class);
                criteria.setFirstResult(page);
                criteria.setMaxResults(pageSize);
                criteria.addOrder(Order.asc("id"));

                items = criteria.list();
            } else {
                Criteria criteria = session.createCriteria(RssItem.class);
                criteria.addOrder(Order.asc("id"));

                items = criteria.list();
            }
        }
        return items;
    }

    @Override
    public RssItem getById(Long id) {
        RssItem result;
        try (Session session = getSession()) {
            String hqlQuery = "from RssItem WHERE ID = :id";
            Query query = session.createQuery(hqlQuery).setParameter("id", id);
            result = (RssItem) query.uniqueResult();
        }
        return result;
    }

    @Override
    public Map<String, Integer> getTopWords(Long item_id) {
        RssItem rssItem = new RssItemDAOImpl().getById(item_id);
        if (rssItem == null) {
            System.err.println("No item with such ID.");
            return null;
        }
        Map<String, Integer> frequency = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        builder.append(rssItem.getTitle() + " ");
        builder.append(rssItem.getDescription());

        String[] words = builder.toString().split("[ .,?!]+");

        Stream.of(words).forEach(string -> {
            Integer previousValue = 1;
            if (!frequency.containsKey(string)) {
                frequency.put(string, previousValue);
            } else if (frequency.containsKey(string)) {
                frequency.put(string, frequency.get(string) + 1);
                previousValue++;
            }
        });

        TreeMap result = sortByValue(frequency);
        return result;
    }

    private TreeMap<String, Integer> sortByValue(Map<String, Integer> map) {
        TreeMap<String, Integer> result = new TreeMap<>((a, b) -> {
            if (map.get(a) >= map.get(b))
                return -1;
            else
                return 1;
        });
        result.putAll(map);

        return result;
    }

    public static void add(RssItem rssItem) {
        try (Session session = getSession()) {
            session.beginTransaction();
            session.save(rssItem);
            session.getTransaction().commit();
        }
    }

    private static Session getSession() {
        return sessionProvider.openSession();
    }



}