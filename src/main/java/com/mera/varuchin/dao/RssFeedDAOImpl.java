package com.mera.varuchin.dao;

import com.mera.varuchin.ServiceORM;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;


public class RssFeedDAOImpl implements RSSfeedDAO {

    @Override
    public void add(RssFeed rssFeed) {
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();

            RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
            session.save(rssFeed);
            rssItemDAO.add(rssFeed.getLink(), rssFeed);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }

    }

    @Override
    public void update(RssFeed rssFeed) {

        RssFeed origin = new RssFeedDAOImpl().getById(rssFeed.getId());
        if (origin.equals(null)) {
            System.err.println("Not found such RSS feed!");
            return;
        } else {
            Session session = null;
            try {
                session = ServiceORM.getSessionFactory().openSession();
                session.beginTransaction();
//
                String hqlUpdateName =
                        "UPDATE RssFeed SET NAME " +
                                "= :newNAME WHERE ID = :ID";
                String hqlUpdateLink =
                        "UPDATE RssFeed SET LINK " +
                                "= :newLINK WHERE ID = :ID";

                if (rssFeed.getName() != null)
                    session.createQuery(hqlUpdateName).setParameter("newNAME", rssFeed.getName())
                            .setParameter("ID", origin.getId()).executeUpdate();

                System.err.println(rssFeed.getLink());
                if (rssFeed.getLink() != null)
                    session.createQuery(hqlUpdateLink).setParameter("newLINK", rssFeed.getLink().toString())
                            .setParameter("ID", origin.getId()).executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (session != null && session.isOpen())
                    session.close();
            }
        }
    }

    @Override
    public void update(RssFeed rssFeed, String name) {
        RssFeed origin = new RssFeedDAOImpl().getById(rssFeed.getId());

        if (origin.equals(null)) {
            System.err.println("Not found such RSS feed.");
            return;
        }
        Session session = null;
        try {
            String hqlUpdate = "UPDATE RssFeed SET NAME = :newName" +
                    " WHERE NAME = :oldName";

            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
            session.createQuery(hqlUpdate).setString("newName", name)
                    .setString("oldName", rssFeed.getName())
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public void update(RssFeed rssFeed, URL url) {
        RssFeed origin = new RssFeedDAOImpl().getById(rssFeed.getId());
        if (origin.equals(null)) {
            System.err.println("Not found RssFeed!");
            return;
        }
        Session session = null;
        try {
            String hqlUpdateFeed = "UPDATE RssFeed SET LINK = :newLink " +
                    "WHERE LINK = :oldLink";

            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
            session.createQuery(hqlUpdateFeed).setString("newLink", url.toString())
                    .setString("oldLink", rssFeed.getLink().toString())
                    .executeUpdate();

            RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
            rssItemDAO.remove(rssFeed.getLink());

            rssItemDAO.add(url, rssFeed);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public void update(RssFeed rssFeed, String name, URL url) {
        RssFeed origin = new RssFeedDAOImpl().getById(rssFeed.getId());
        if (origin.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }

        Session session = null;
        try {
            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
            String hqlUpdateName =
                    "UPDATE RssFeed SET NAME = :newName WHERE NAME = :oldName";
            String hqlUpdateLink =
                    "UPDATE RssFeed SET LINK = :newLink WHERE LINK = :oldLink";
            session.createQuery(hqlUpdateName).setString("newName", name)
                    .setString("oldName", rssFeed.getName())
                    .executeUpdate();
            session.getTransaction().commit();

            session.createQuery(hqlUpdateLink).setString("newLink", url.toString())
                    .setString("oldLink", rssFeed.getLink().toString())
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }


    @Override
    public void remove(Long id) {
        RssFeed rssFeed = new RssFeedDAOImpl().getById(id);

        if (rssFeed.equals(null)) {
            System.err.println("No such feed.");
            return;
        }

        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(rssFeed);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public void registerInBulk(File inputFile) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputFile);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("source");

            if (nodeList.getLength() > 0) {
                IntStream.range(0, nodeList.getLength()).forEach(index -> {

                    Element entry = (Element) nodeList.item(index);
                    Element nameElem = (Element) entry.getElementsByTagName("name").item(0);
                    Element linkElem = (Element) entry.getElementsByTagName("link").item(0);

                    String name = nameElem.getFirstChild().getTextContent();
                    String link = linkElem.getFirstChild().getTextContent();

                    RssFeedDAOImpl rssFeedDAO = new RssFeedDAOImpl();
                    RssFeed rssFeed = null;
                    try {
                        rssFeed = new RssFeed(name, new URL(link));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    rssFeedDAO.add(rssFeed);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh(RssFeed rssFeed) {
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            session.delete(rssFeed);
            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }

        RssFeedDAOImpl rssFeedDAO = new RssFeedDAOImpl();
        rssFeedDAO.add(rssFeed);
    }

    @Override
    public RssFeed getById(Long id) {
        RssFeed result = null;
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            result = (RssFeed) session.get(RssFeed.class, id);
            System.err.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return result;
    }

    @Override
    public RssFeed getByLink(URL link) {
        Session session = null;
        try {
            System.out.println("ERR");
            session = ServiceORM.getSessionFactory().openSession();
            String hqlQuery = "FROM RssFeed WHERE LINK = :link";
            Query query = session.createQuery(hqlQuery).setParameter("link", link.toString());
            return (RssFeed) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return null;
    }

    @Override
    public RssItem getBySource(Long feed_id, Long item_id) {
        RssFeed rssFeed = new RssFeedDAOImpl().getById(feed_id);
        System.out.println(rssFeed);


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
    public List<RssFeed> getFeedsByName(int page, int pageSize, String name) {
        List<RssFeed> feeds = new ArrayList<>();
        Session session = null;

        try {
            String param = "%" + name + "%";
            session = ServiceORM.getSessionFactory().openSession();
            Query query = session.createQuery("FROM RssFeed WHERE lower(NAME) LIKE lower" +
                    "(:NAME) ORDER BY NAME ASC");

            query.setParameter("NAME", param);
            query.setFirstResult(page);
            query.setMaxResults(pageSize);

            System.err.println(query.toString());

            System.err.println(feeds);
            feeds = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return feeds;
    }


    @Override
    public List<RssItem> getNewsFromSource(URL source) {
        List<RssItem> news = new ArrayList<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpHost proxy = new HttpHost("proxy.merann.ru", 8080, "http");
            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();

            System.out.println(source.toURI().toString());
            HttpGet httpGet = new HttpGet(source.toURI());
            httpGet.setConfig(config);

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream inputStream = entity.getContent();
                try {
                    DocumentBuilderFactory documentBuilderFactory =
                            DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    Document document = documentBuilder.parse(inputStream);
                    Element element = document.getDocumentElement();

                    NodeList nodeList = element.getElementsByTagName("item");

                    if (nodeList.getLength() > 0) {
                        IntStream.range(0, nodeList.getLength()).forEach(index -> {
                            Element entry = (Element) nodeList.item(index);

                            Element titleElem = (Element) entry
                                    .getElementsByTagName("title").item(0);

                            Element descriptionElem = (Element) entry
                                    .getElementsByTagName("description").item(0);

                            Element pubDateElem = (Element) entry
                                    .getElementsByTagName("pubDate").item(0);

                            Element linkElem = (Element) entry
                                    .getElementsByTagName("link").item(0);

                            String title = titleElem.getFirstChild().getTextContent();
                            String description = descriptionElem.getFirstChild()
                                    .getTextContent();
                            //переделать в number (instant)или прост Long (из instant->Long)
                            Date pubDate = new Date(pubDateElem.getFirstChild().getTextContent());

                            URL link = null;
                            try {
                                link = new URL(linkElem.getFirstChild().getNodeValue());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            RssItem rssItem = new RssItem(title, description, pubDate, link);
                            news.add(rssItem);
                        });
                    }
                } finally {
                    response.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return news;
    }

    @Override
    public List<RssFeed> getAllRegisteredFeeds() {
        Session session = null;
        List<RssFeed> rssFeeds = new ArrayList<>();

        try {
            session = ServiceORM.getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(RssFeed.class);

            rssFeeds = (ArrayList<RssFeed>) criteria.list();
            System.out.println(rssFeeds);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return rssFeeds;
    }
}
