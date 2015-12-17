package com.mera.varuchin.dao;

import com.mera.varuchin.ServiceORM;
import com.mera.varuchin.rss.RssItem;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    @Override
    public void add(URL link) {
        ArrayList<RssItem> rssItems = new ArrayList<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpHost proxy = new HttpHost("proxy.merann.ru", 8080, "http");
            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();
            HttpGet httpGet = new HttpGet(link.toURI());
            httpGet.setConfig(config);

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                try {
                    //inputStream.read();
                    DocumentBuilderFactory documentBuilderFactory =
                            DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    Document document = documentBuilder.parse(inputStream);
                    Element element = document.getDocumentElement();

//                    Reader reader = new InputStreamReader(inputStream);
//                    InputSource is = new InputSource(reader);
//
//                    Document document = documentBuilder.parse(is);
//                    // document.getDocumentElement().normalize();
//                    Element element = document.getDocumentElement();
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
                            String description = descriptionElem.getFirstChild().getTextContent();
                            Date pubDate = new Date(pubDateElem.getFirstChild().getTextContent());

                            URL url = null;
                            try {
                                url = new URL(linkElem.getFirstChild().getNodeValue());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            RssItem rssItem = new RssItem(title, description, pubDate, url);
                            rssItems.add(rssItem);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rssItems.stream().forEach(item -> add(item));
    }

    @Override
    public void remove(URL link) {
        RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
        Collection<RssItem> linkedItems = rssItemDAO.getByLink(link);
        Session session = null;

        if (linkedItems == null) {
            System.err.println("Not found item to remove.");
            return;
        }
        try {
            session = ServiceORM.getSessionFactory().openSession();
            session.beginTransaction();
            String hqlDelete = "DELETE FROM RssItem WHERE LINK = :link";
            System.out.println("DELETING");

            System.err.println(link.toString());
            session.createQuery(hqlDelete).setParameter("link", link.toString()).executeUpdate();
            session.getTransaction().commit();
            System.err.println("Deleted.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    @Override
    public Collection<RssItem> getItemsWithLink(URL link) {
        Collection<RssItem> items = new ArrayList<>();

        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            Query query = session.createQuery("FROM RssItem WHERE LINK = :link");

            items = query.setParameter("link", link.toString()).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return items;
    }

    @Override
    public Collection<RssItem> getAllItems() {
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
        System.out.println(items);
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
    public Collection<RssItem> getByLink(URL link) {
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            String hqlQuery = "FROM RssItem WHERE LINK = :link";
            Query query = session.createQuery(hqlQuery).setString("link", link.toString());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return null;
    }

    @Override
    public RssItem getByTitle(String title) {
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            String hqlQuery = "FROM RssItem WHERE TITLE = :title";
            Query query = session.createQuery(hqlQuery).setParameter("title", title);
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
        Collection<RssItem> collection = getAllItems();
        Map<String, URL> result = new HashMap<>();

        //System.out.println(collection);
        collection.stream().forEach(rssItem -> {
            result.put(rssItem.getTitle(), rssItem.getLink());
        });
        System.out.println(result);
        return result;
    }

    @Override
    public RssItem getBySource(String title, URL link) {
        Map<String, URL> sources = getAllSourcesRss();

        if (sources.containsKey(title) && sources.containsValue(link)) {
            RssItem item = new RssItemDAOImpl().getByTitle(title);
            return item;
        }
        return null;
    }
}
