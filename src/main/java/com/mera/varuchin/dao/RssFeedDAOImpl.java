package com.mera.varuchin.dao;

import com.mera.varuchin.ServiceORM;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.hibernate.Query;
import org.hibernate.Session;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;


public class RssFeedDAOImpl implements RSSfeedDAO {

    @Override
    public void add(RssFeed rssFeed) {
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(rssFeed);

            System.err.println("HERE ");
            RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
            rssItemDAO.add(rssFeed.getLink());
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

//    @Override
//    public void add(URL feedURL, String name) {
//        ArrayList<RssFeed> rssFeeds = new ArrayList<>();
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        try {
//            HttpHost proxy = new HttpHost("proxy.merann.ru", 8080, "http");
//            RequestConfig config = RequestConfig.custom()
//                    .setProxy(proxy)
//                    .build();
//            HttpGet httpGet = new HttpGet(feedURL.toURI());
//            httpGet.setConfig(config);
//
//            CloseableHttpResponse response = httpClient.execute(httpGet);
//            System.out.println(response.getStatusLine());
//            HttpEntity httpEntity = response.getEntity();
//
//            System.out.println(response.getStatusLine());
//
//            if (httpEntity != null) {
//                InputStream inputStream = httpEntity.getContent();
//                try {
//                    //inputStream.read();
//                    DocumentBuilderFactory documentBuilderFactory =
//                            DocumentBuilderFactory.newInstance();
//                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//
//                    Document document = documentBuilder.parse(inputStream);
//                    Element element = document.getDocumentElement();
//                    System.out.println(element);
//
////                    Reader reader = new InputStreamReader(inputStream);
////                    InputSource is = new InputSource(reader);
////
////                    Document document = documentBuilder.parse(is);
////                    // document.getDocumentElement().normalize();
////                    Element element = document.getDocumentElement();
//
//                    NodeList nodeList = element.getElementsByTagName("item");
//                    System.out.println(nodeList.getLength());
//
//                    if (nodeList.getLength() > 0) {
//
//                        IntStream.range(0, nodeList.getLength()).forEach(index -> {
//
//                            System.out.println(nodeList.item(index));
//                            Element entry = (Element) nodeList.item(index);
//
//
//                            Element titleElem = (Element) entry
//                                    .getElementsByTagName("title").item(0);
//
//                            Element descriptionElem = (Element) entry
//                                    .getElementsByTagName("description").item(0);
//
//                            Element pubDateElem = (Element) entry
//                                    .getElementsByTagName("pubDate").item(0);
//
//                            Element linkElem = (Element) entry
//                                    .getElementsByTagName("link").item(0);
//
//                            String title = titleElem.getFirstChild().getTextContent();
//                            System.out.println(title);
//                            String description = descriptionElem.getFirstChild().getTextContent();
//                            System.out.println(description);
//                            Date pubDate = new Date(pubDateElem.getFirstChild().getTextContent());
//
//
//                            URL link = null;
//                            try {
//                                link = new URL(linkElem.getFirstChild().getNodeValue());
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            }
//                            RssItem rssItem = new RssItem(title, description, pubDate, link);
//                            RssFeed rssFeed = new RssFeed(rssItem, name);
//                            rssFeeds.add(rssFeed);
//                        });
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    response.close();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        rssFeeds.stream().forEach(feed
//                -> add(feed));
//    }

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

            rssItemDAO.add(url);
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
    public RssItem getBySource(String title, URL link) {
        Collection<RssFeed> feeds = new RssFeedDAOImpl().getAllRegisteredFeeds();
        RssItem rssItem = null;

        boolean hasLink = feeds.stream().anyMatch(feed -> {
            if (feed.getLink() == link)
                return true;
            return false;
        });
        if (hasLink) {
            rssItem = new RssItemDAOImpl().getBySource(title, link);
        }
        return rssItem;
    }

    @Override
    public Collection<RssFeed> getRssSortedByName(String name) {
        Collection<RssFeed> feeds = new ArrayList<>();
        Session session = null;
        try {

            String param = "%" + name.toUpperCase() + "%";
            session = ServiceORM.getSessionFactory().openSession();
            Query query = session.createQuery("FROM RssFeed WHERE NAME LIKE " +
                    ":NAME ORDER BY NAME ASC");

            query.setParameter("NAME", param);
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
    public Collection<RssFeed> getAllRegisteredFeeds() {
        Session session = null;
        Collection<RssFeed> rssFeeds = new ArrayList<>();

        try {
            session = ServiceORM.getSessionFactory().openSession();
            Query query = session.createQuery("from RssFeed");
            rssFeeds = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return rssFeeds;
    }

    //поулчить все айтемы с фида
    @Override
    public Collection<RssItem> getAllItems(Long id) {
        Collection<RssItem> items = null;

        RssFeed rssFeed = new RssFeedDAOImpl().getById(id);
        if (rssFeed.equals(null)) {
            System.err.println("No feed with such ID");
            return null;
        }

        items = new RssItemDAOImpl().getItemsWithLink(rssFeed.getLink());
        System.out.println(items);
        return items;
    }


//    @Override
//    public RssItemRssItem> getItem(Long item_id){
//        Session session = null;
//        Collection<RssItem>  items = new ArrayList<>();
//
//        try{
//            session = ServiceORM.getSessionFactory().openSession();
//            String hqlQuery = "FROM RssFeed WHERE ITEM_ID = :link";
//            Query query = session.createQuery(hqlQuery).setParameter("link", item_id.toString());
//            if(query.uniqueResult().equals(null)) {
//                System.err.println("No item with such ID.");
//                return null;
//            }
//            else{
//                RssItem rssItem = (RssItem) query.uniqueResult();
//            }
//        }
//        return items;
//    }
}
