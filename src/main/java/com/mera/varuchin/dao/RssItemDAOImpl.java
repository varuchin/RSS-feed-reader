package com.mera.varuchin.dao;

import com.mera.varuchin.ServiceORM;
import com.mera.varuchin.pagination.Pageable;
import com.mera.varuchin.rss.RssItem;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;
import  org.apache.http.client.*;

public class RssItemDAOImpl implements RssItemDAO {


    @Override
    public void add(RssItem rssItem) {
        Session session = null;
        try {
            session = ServiceORM.getSessionFactory().openSession();
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
//1)p0

    @Override
    public void add(URL feedURL, String name) {
//        int count = 0;
        ArrayList<RssItem> rssItems = new ArrayList<>();
        try {

            
             //готов
             //переделывать
//            HttpURLConnection connection = (HttpURLConnection) feedURL.openConnection();

//            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                InputStream inputStream = connection.getInputStream();
            //InputStream inputStream = httpClient.getInputStream();

            DocumentBuilderFactory documentBuilderFactory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document document = documentBuilder.parse(inputStream);
                Element element = document.getDocumentElement();

                NodeList nodeList = element.getElementsByTagName("item");

                if (nodeList.getLength() > 0) {
                    Stream.of(nodeList).parallel().forEach((node) -> {

                        //тут трабл
                        Element entry = (Element) nodeList.item(0);
                        //переделать цикл

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
            } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rssItems.stream().forEach(rssItem -> add(rssItem));
        System.out.println(rssItems);
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
            session = ServiceORM.getSessionFactory().openSession();
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
        RssItem origin = new RssItemDAOImpl().getByLink(rssItem.getLink());
        System.out.println(origin);
        if (origin.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        } else {
            Session session = null;
            try {
                session = ServiceORM.getSessionFactory().openSession();
                session.beginTransaction();
                String hqlUpdateName =
                        "UPDATE RssItem SET NAME " +
                                "= :newNAME WHERE ID = :ID";
                String hqlUpdateTitle =
                        "UPDATE RssItem SET TITLE " +
                                "= :newTITLE WHERE ID = :ID";
                String hqlUpdateDescription =
                        "UPDATE RssItem SET DESCRIPTION " +
                                "= :newDESCRIPTION" + " WHERE ID = :ID";
                String hqlUpdatePubDate =
                        "UPDATE RssItem SET PUB_DATE " +
                                "= :newPUB_DATE WHERE ID = :ID";
                String hqlUpdateLink =
                        "UPDATE RssItem SET LINK " +
                                "= :newLINK WHERE ID = :ID";

                if (rssItem.getName() != null)
                    session.createQuery(hqlUpdateName).setParameter("newNAME", rssItem.getName())
                            .setParameter("ID", origin.getId()).executeUpdate();

                if (rssItem.getTitle() != null)
                    session.createQuery(hqlUpdateTitle).setParameter("newTITLE", rssItem.getTitle())
                            .setParameter("ID", origin.getId()).executeUpdate();

                if (rssItem.getDescription() != null)
                    session.createQuery(hqlUpdateDescription).setParameter("newDESCRIPTION",
                            rssItem.getDescription())
                            .setParameter("ID", origin.getId()).executeUpdate();

                if (rssItem.getPubDate() != null)
                    session.createQuery(hqlUpdatePubDate).setParameter("newPUB_DATE", rssItem.getPubDate()
                            .toString()).setParameter("ID", origin.getId()).executeUpdate();

                if (rssItem.getLink() != null)
                    session.createQuery(hqlUpdateLink).setParameter("newLINK", rssItem.getLink().toString())
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
    public void update(RssItem rssItem, String name) {
        RssItem origin = new RssItemDAOImpl().getById(rssItem.getId());
        if (origin.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        Session session = null;
        try {
            String hqlUpdate = "UPDATE RssItem SET NAME = :newName" +
                    " WHERE NAME = :oldName";

            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
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
        RssItem origin = new RssItemDAOImpl().getById(rssItem.getId());
        if (origin.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        Session session = null;
        try {
            String hqlUpdate = "UPDATE RssItem SET LINK = :newLink " +
                    "WHERE LINK = :oldLink";

            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
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
        RssItem origin = new RssItemDAOImpl().getById(rssItem.getId());
        if (origin.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }

        Session session = null;
        try {
            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
            String hqlUpdateName =
                    "UPDATE RssItem SET NAME = :newName WHERE NAME = :oldName";
            String hqlUpdateLink =
                    "UPDATE RssItem SET LINK = :newLink WHERE LINK = :oldLink";
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
    public RssItem getById(Long id) {
        RssItem result = null;
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            // session.beginTransaction();
            result = (RssItem) session.get(RssItem.class, id);
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
    public Collection<RssItem> getAllRss() {
        Collection<RssItem> items = new ArrayList<>();
        //Collection<String> result = new ArrayList<>();
        Session session = null;
        try {
            session = ServiceORM.getSessionFactory().openSession();
            Query query = session.createQuery("FROM RssItem");
            items = query.list();
            //result = XmlService.buildXML(items);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR ERROR ERROR");
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return items;
    }

    @Override
    public Collection<RssItem> getRssSortedByName(String name) {
        Collection<RssItem> items = new ArrayList<>();
        Session session = null;
        try {

            String param = "%" + name + "%";
            session = ServiceORM.getSessionFactory().openSession();
            Query query = session.createQuery("FROM RssItem WHERE NAME LIKE " +
                    ":NAME ORDER BY NAME ASC");

//            Criteria criteria = session.createCriteria(RssItem.class);
//            criteria.add(Restrictions.ilike("NAME", param));

            query.setParameter("NAME", param);
            System.err.println(query.toString());

            System.err.println(items);
            items = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return items;
    }

//    @Override
//    public SourceRSS getByRssSource(RssItem rssItem) {
//        RssItem origin = new RssItemDAOImpl().getById(rssItem.getId());
//
//        if (origin == null) {
//            System.err.println("No such RSS Item.");
//            return null;
//        } else
//            return rssItem.getSourceRSS();
//    }

    @Override
    public RssItem getByLink(URL link) {
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            String hqlQuery = "FROM RssItem WHERE LINK = :link";
            Query query = session.createQuery(hqlQuery).setString("link", link.toString());
            return (RssItem) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return null;
    }

    //    cделать отображение только 5-ти слов

    @Override
    public Map<String, Integer> getTopWords(RssItem rssItem) {

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

        SortedMap<String, Integer> result = new TreeMap<>();
        result.putAll(frequency);


        return result;
    }

    @Override
    public Map<String, URL> getAllSourcesRss() {
        Collection<RssItem> collection = getAllRss();
        Map<String, URL> result = new HashMap<>();

        //System.out.println(collection);
        collection.stream().parallel().forEach(rssItem -> {
            result.put(rssItem.getTitle(), rssItem.getLink());
        });
        System.out.println(result);
        return result;
    }

    @Override
    public RssItem getBySource(String title, URL link) {
        Map<String, URL> sources = getAllSourcesRss();

        if (sources.containsKey(title) && sources.containsValue(link)) {
            RssItem item = new RssItemDAOImpl().getByLink(link);
            return item;
        }
        return null;
    }

    //думать
    @Override
    public Pageable<RssItem> getPaginatedListFiltered(String name) {
        Collection<RssItem> collection = getRssSortedByName("asf");
        List items;
        if (collection instanceof List)
            items = (List) collection;
        else
            items = new ArrayList(collection);

        Pageable<RssItem> paginator = new Pageable<>(items);

        return paginator;
    }
}
