package com.mera.varuchin.dao;

import com.mera.varuchin.ServiceORM;
import com.mera.varuchin.rss.RssExecutor;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.net.URL;
import java.util.List;


public class RssFeedDAOImpl implements RSSfeedDAO {

    @Override
    public void add(RssFeed rssFeed) {
        RssFeed feed = new RssFeedDAOImpl().getByLink(rssFeed.getLink());
        if (feed != null) {
            return;
        }

        RssExecutor rssExecutor = new RssExecutor();
        Runnable task = () -> {
            Session session = null;
            try {
                session = ServiceORM.getSessionFactory().openSession();
                session.beginTransaction();
                session.save(rssFeed);

                RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
                rssItemDAO.add(rssFeed);

                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (session != null && session.isOpen())
                    session.close();
            }
        };

        rssExecutor.run(task);
    }

    @Override
    public void update(RssFeed rssFeed) {
        try (Session session = ServiceORM.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(rssFeed);
            transaction.commit();
        }
    }


    @Override
    public void remove(Long id) {
        RssFeed rssFeed = new RssFeedDAOImpl().getById(id);

        if (rssFeed.equals(null)) {
            System.err.println("No such feed.");
            return;
        }
        try (Session session = ServiceORM.openSession()) {
            session.beginTransaction();
            session.delete(rssFeed);
            session.getTransaction().commit();
        }
    }

    private static void deleteItems(RssFeed rssFeed){
        try(Session session = ServiceORM.openSession()){
            session.beginTransaction();
            String hql = "From RssItem WHERE FEED_ID = :feed_id";
            Query query = session.createQuery(hql);
            query.setParameter("FEED_ID", rssFeed.getId());
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public void refresh(RssFeed rssFeed) {
        RssExecutor rssExecutor = new RssExecutor();
        RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();

        Runnable task = () -> {
            deleteItems(rssFeed);
            rssItemDAO.add(rssFeed);
        };

        rssExecutor.run(task);
    }

    @Override
    public RssFeed getById(Long id) {
        RssFeed result;

        try (Session session = ServiceORM.openSession()) {
            result = (RssFeed) session.get(RssFeed.class, id);
            System.err.println(result);
        }
        return result;
    }

    @Override
    public RssFeed getByLink(URL link) {
        try (Session session = ServiceORM.openSession()) {
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
        System.out.println(rssItem);
        return rssItem;
    }


    @Override
    public List<RssFeed> getFeeds(Integer page, Integer pageSize, String name) {
        List<RssFeed> feeds;
        try (Session session = ServiceORM.openSession()) {
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

}
