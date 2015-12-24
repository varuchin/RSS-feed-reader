package com.mera.varuchin.dao;

import com.mera.varuchin.SessionProvider;
import com.mera.varuchin.parsers.RssParser;
import com.mera.varuchin.rss.RssExecutor;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

public class RssItemDAOImpl implements RssItemDAO {

    @Override
    public void add(RssFeed rssFeed) {
        RssExecutor rssExecutor = new RssExecutor();

        Runnable task = () -> {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            try {
                HttpHost proxy = new HttpHost("proxy.merann.ru", 8080, "http");
                RequestConfig config = RequestConfig.custom()
                        .setProxy(proxy)
                        .build();
                HttpGet httpGet = new HttpGet(rssFeed.getLink().toURI());
                httpGet.setConfig(config);

                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();

                if (httpEntity != null) {
                    RssParser parser = new RssParser();
                    InputStream inputStream = httpEntity.getContent();
                    List<RssItem> items = parser.parseItems(inputStream);
                    items.forEach(item -> {
                        item.setRssFeed(rssFeed);
                        rssFeed.addItem(item);
                        add(item);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        rssExecutor.run(task);
    }

    @Override
    public void remove(RssItem rssItem) {
        try (Session session = SessionProvider.openSession()) {
            session.beginTransaction();
            session.delete(rssItem);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<RssItem> getItems(Integer page, Integer pageSize) {
        List<RssItem> items;

        try (Session session = SessionProvider.openSession()) {
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
        try (Session session = SessionProvider.openSession()) {
            String hqlQuery = "from RssItem WHERE ID = :id";
            Query query = session.createQuery(hqlQuery).setParameter("id", id);
            result = (RssItem) query.uniqueResult();
        }
        return result;
    }

    @Override
    public List<RssItem> getAllItemsWithId(Long feed_id) {
        List<RssItem> result;
        try (Session session = SessionProvider.openSession()) {
            String hql = "FROM RssItem WHERE FEED_ID = :feed_id";
            Query query = session.createQuery(hql);
            query.setParameter("feed_id", feed_id);
            result = query.list();
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
        try (Session session = SessionProvider.openSession()) {
            session.beginTransaction();
            session.save(rssItem);
            session.getTransaction().commit();
        }
    }

//    @Override
//    public Map<String, URL> getAllSourcesRss() {
//        List<RssItem> collection = getAllItems();
//        Map<String, URL> result = new HashMap<>();
//
//        //System.out.println(collection);
//        collection.stream().forEach(rssItem -> {
//            result.put(rssItem.getTitle(), rssItem.getLink());
//        });
//        System.out.println(result);
//        return result;
//    }
}
