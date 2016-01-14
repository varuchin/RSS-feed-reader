package com.mera.varuchin.dao;

import com.mera.varuchin.SessionProvider;
import com.mera.varuchin.parsers.ItemParser;
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
import org.hibernate.Transaction;

import java.io.InputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;


public class RssFeedDAOImpl implements RssFeedDAO {

    private SessionProvider sessionProvider = new SessionProvider();
    public RssFeedDAOImpl(){}

    @Override
    public void add(RssFeed rssFeed) {
        RssFeed feed = new RssFeedDAOImpl().getByLink(rssFeed.getLink());
        if (feed != null) {
            return;
        }
        try (Session session = getSession()) {
            session.beginTransaction();
            session.save(rssFeed);
            session.getTransaction().commit();

            session.beginTransaction();
            RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
            rssItemDAO.add(rssFeed);

            session.getTransaction().commit();
        }
    }

    @Override
    public void update(RssFeed rssFeed) {
        try (Session session = getSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(rssFeed);
            transaction.commit();
        }
    }

    @Override
    public void remove(Long id) {
        RssFeed rssFeed = new RssFeedDAOImpl().getById(id);

        try (Session session = getSession()) {
            session.beginTransaction();
            session.delete(rssFeed);
            session.getTransaction().commit();
        }
    }

    @Override
    public void refresh(RssFeed rssFeed) {
        deleteItems(rssFeed);
        addItems(rssFeed);
        rssFeed.setModificationTime(ZonedDateTime.now());
    }

    @Override
    public RssFeed getById(Long id) {
        RssFeed result;

        try (Session session = getSession()) {
            result = (RssFeed) session.get(RssFeed.class, id);
        }
        return result;
    }

    @Override
    public RssFeed getByLink(URL link) {
        try (Session session = getSession()) {
            String hqlQuery = "FROM RssFeed WHERE LINK = :link";
            Query query = session.createQuery(hqlQuery).setParameter("link", link.toString());
            return (RssFeed) query.uniqueResult();
        }
    }

    @Override
    public RssItem getBySource(Long feed_id, Long item_id) {
        RssFeed rssFeed = new RssFeedDAOImpl().getById(feed_id);

        if (rssFeed == null) {
            System.err.println("No feed with such ID.");
            return null;
        }
        RssItem rssItem = new RssItemDAOImpl().getById(item_id);
        if (rssItem == null) {
            System.err.println("No item with such ID.");
            return null;
        }
        return rssItem;
    }


    @Override
    public List<RssFeed> getFeeds(Integer page, Integer pageSize, String name) {
        List<RssFeed> feeds;
        try (Session session = getSession()) {
            if (name != null) {
                String param = "%" + name + "%";
                Query query = session.createQuery("FROM RssFeed WHERE lower(NAME) LIKE lower" +
                        "(:NAME) ORDER BY NAME ASC");

                query.setParameter("NAME", param);
                query.setFirstResult(page);
                query.setMaxResults(pageSize);

                feeds = query.list();
            } else if (page != null && pageSize != null) {
                Criteria criteria = session.createCriteria(RssFeed.class);
                criteria.setFirstResult(page);
                criteria.setMaxResults(pageSize);
                feeds = criteria.list();
            } else {
                Criteria criteria = session.createCriteria(RssFeed.class);
                feeds = criteria.list();
            }
        }

        return feeds;
    }

    private void deleteItems(RssFeed rssFeed) {
        try (Session session = getSession()) {
            session.beginTransaction();

            String hql = "DELETE FROM RssItem WHERE FEED_ID = :feed_id";
            Query query = session.createQuery(hql);

            query.setParameter("feed_id", rssFeed.getId());
            query.executeUpdate();
            System.err.println("Удалено");
            session.getTransaction().commit();
        }
    }

    private void addItems(RssFeed rssFeed) {
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
                ItemParser parser = new ItemParser();
                InputStream inputStream = httpEntity.getContent();
                List<RssItem> items = parser.parseItems(inputStream);

                rssFeed.setItems(new HashSet<>());

                items.forEach(item -> {
                    item.setRssFeed(rssFeed);
                    RssItemDAOImpl.add(item);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Session getSession() {
        return sessionProvider.openSession();
    }

}
