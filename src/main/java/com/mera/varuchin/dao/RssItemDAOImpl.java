package com.mera.varuchin.dao;

import com.mera.varuchin.HibernateUtil;
import com.mera.varuchin.rss.RssItem;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;


public class RssItemDAOImpl implements RssItemDAO {


    @Override
    public void add(RssItem rssItem) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(rssItem);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public void add(URL feedURL, String name) {
        ArrayList<RssItem> rssItems = new ArrayList<>();
        try {
            HttpURLConnection connection = (HttpURLConnection) feedURL.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();

                DocumentBuilderFactory documentBuilderFactory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document document = documentBuilder.parse(inputStream);
                Element element = document.getDocumentElement();

                NodeList nodeList = element.getElementsByTagName("item");

                if (nodeList.getLength() > 0) {
                    Stream.of(nodeList).parallel().forEach((node) -> {
                        Element entry = (Element) nodeList.item(0);

                        Element titleElem = (Element) entry
                                .getElementsByTagName("title").item(0);

                        Element descriptionElem = (Element) entry
                                .getElementsByTagName("description").item(0);

                        Element pubDateElem = (Element) entry
                                .getElementsByTagName("pubDate").item(0);

                        Element linkElem = (Element) entry
                                .getElementsByTagName("link").item(0);

                        String title = titleElem.getFirstChild().getNodeValue();
                        String description = descriptionElem.getFirstChild().getNodeValue();
                        Date pubDate = new Date(pubDateElem.getFirstChild().getNodeValue());

                        URL link = null;
                        try {
                            link = new URL(linkElem.getFirstChild().getNodeValue());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        RssItem rssItem = new RssItem(name, title, description, pubDate, link);
                        rssItems.add(rssItem);
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        rssItems.stream().forEach(rssItem -> add(rssItem));
    }

    @Override
    public void remove(Long id) {
        RssItem rssItem = new RssItemDAOImpl().getById(id);
        if (rssItem.equals(null)) {
            System.err.println("No such element.");
            return;
        }
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(rssItem);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public void update(RssItem rssItem) {
        if (rssItem.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            if (!rssItem.equals(null)) {
                String hqlUpdateName =
                        "UPDATE RSS SET RSS.NAME " +
                                "= :newNAME WHERE RSS.ID = :ID";
                String hqlUpdateTitle =
                        "UPDATE RSS SET RSS.TITLE " +
                                "= :newTITLE WHERE RSS.ID = :ID";
                String hqlUpdateDescription =
                        "UPDATE RSS SET RSS.DESCRIPTION " +
                                "= :newDESCRIPTION" + " WHERE RSS.ID = :ID";
                String hqlUpdatePubDate =
                        "UPDATE RSS SET RSS.PUB_DATE " +
                                "= :newPUB_DATE WHERE RSS.ID = :ID";
                String hqlUpdateLink =
                        "UPDATE RSS SET RSS.LINK " +
                                "= :newLink WHERE RSS.ID = :ID";

                if (rssItem.getName() != null)
                    session.createQuery(hqlUpdateName).setString("newNAME", rssItem.getName())
                            .setString("ID", rssItem.getId().toString()).executeUpdate();

                if (rssItem.getTitle() != null)
                    session.createQuery(hqlUpdateTitle).setString("newTITLE", rssItem.getTitle())
                            .setString("ID", rssItem.getId().toString()).executeUpdate();

                if (rssItem.getDescription() != null)
                    session.createQuery(hqlUpdateDescription).setString("newDESCRIPTION",
                            rssItem.getDescription())
                            .setString("ID", rssItem.getId().toString()).executeUpdate();

                if (rssItem.getPubDate() != null)
                    session.createQuery(hqlUpdatePubDate).setString("newPUB_DATE", rssItem.getPubDate()
                            .toString()).setString("ID", rssItem.getId().toString()).executeUpdate();

                if (rssItem.getLink() != null)
                    session.createQuery(hqlUpdateLink).setString("newLINK", rssItem.getLink().toString())
                            .setString("ID", rssItem.getId().toString()).executeUpdate();

                session.getTransaction().commit();

            } else if (session != null && session.isOpen())
                session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public void update(RssItem rssItem, String name) {
        if (rssItem.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        Session session = null;
        try {
            String hqlUpdate = "UPDATE RSS SET RSS.NAME = :newName WHERE RSS.NAME = :oldName";

            session = HibernateUtil.getSessionFactory().openSession();
            session.createQuery(hqlUpdate).setString("newName", name)
                    .setString("oldName", rssItem.getName())
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
    public void update(RssItem rssItem, URL url) {
        if (rssItem.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        Session session = null;
        try {
            String hqlUpdate = "UPDATE RSS SET RSS.LINK = :newLink WHERE RSS.LINK = :oldLink";

            session = HibernateUtil.getSessionFactory().openSession();
            session.createQuery(hqlUpdate).setString("newLink", url.toString())
                    .setString("oldLink", rssItem.getLink().toString())
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    //��� ������� ����� � ����� ������?
    @Override
    public void update(RssItem rssItem, String name, URL url) {
        if (rssItem.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hqlUpdateName =
                    "UPDATE RSS SET RSS.NAME = :newName WHERE RSS.NAME = :oldName";
            String hqlUpdateLink =
                    "UPDATE RSS SET RSS.LINK = :newLink WHERE RSS.LINK = :oldLink";
            session.createQuery(hqlUpdateName).setString("newName", name)
                    .setString("oldName", rssItem.getName())
                    .executeUpdate();
            session.getTransaction().commit();

            session.createQuery(hqlUpdateLink).setString("newLink", url.toString())
                    .setString("oldLink", rssItem.getLink().toString())
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
    public RssItem getById(long id) {
        RssItem result = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            result = (RssItem) session.get(RssItem.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }

        return result;
    }


    @Override
    public Collection<RssItem> getAllRss() {
        Collection<RssItem> result = new ArrayList<>();
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from RSS");
            result = query.list();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR ERROR ERROR");
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return result;
    }

    @Override
    public RssItem getByRssSource(RssItem rssItem) {
        return null;
    }

    @Override
    public Collection<String> getTopWords(RssItem rssItem) {
        return null;
    }

    @Override
    public Collection<RssItem> getPaginatedList() {
        return null;
    }
}
