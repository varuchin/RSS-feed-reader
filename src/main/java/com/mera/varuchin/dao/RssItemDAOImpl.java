package com.mera.varuchin.dao;

import com.mera.varuchin.ServiceORM;
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
import java.util.concurrent.Callable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RssItemDAOImpl implements RssItemDAO {


    @Override
    public void add(RssItem rssItem) {
        RssExecutor rssExecutor = new RssExecutor();
        Runnable task = () -> {
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
        };

        rssExecutor.run(task);
    }

    @Override
    public void add(URL link, RssFeed rssFeed) {
        RssExecutor rssExecutor = new RssExecutor();
        Runnable task = () -> {
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
                                String description = descriptionElem.getFirstChild().getTextContent();
                                Date pubDate = new Date(pubDateElem.getFirstChild().getTextContent());

                                URL url = null;
                                try {
                                    url = new URL(linkElem.getFirstChild().getNodeValue());
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                RssItem rssItem = new RssItem(title, description, pubDate, url);
                                rssItem.setRssFeed(rssFeed);
                                rssFeed.addItem(rssItem);
                                add(rssItem);
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
        };

        rssExecutor.run(task);
    }

    @Override
    public void remove(RssItem rssItem) {
        RssExecutor rssExecutor = new RssExecutor();
        Runnable task = () -> {
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
        };

        rssExecutor.run(task);
    }

    @Override
    public void remove(URL link) {
        RssExecutor rssExecutor = new RssExecutor();
        Runnable task = () -> {
            RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
            List<RssItem> linkedItems = rssItemDAO.getByLink(link);
            Session session = null;

            if (linkedItems == null) {
                System.err.println("Not found item to remove.");
                return;
            }
            try {
                session = ServiceORM.getSessionFactory().openSession();
                session.beginTransaction();
                String hqlDelete = "DELETE FROM RssItem WHERE LINK = :link";

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
        };

        rssExecutor.run(task);
    }

    @Override
    public List<RssItem> getAllItems() {
        RssExecutor rssExecutor = new RssExecutor();
        List<RssItem> result;
        Callable<List<RssItem>> task = () -> {
            List<RssItem> items = new ArrayList<>();
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
        };

        result = rssExecutor.getItems(task);
        return result;
    }

    @Override
    public RssItem getById(Long id) {
        RssExecutor rssExecutor = new RssExecutor();
        RssItem item;
        Callable<RssItem> task = () -> {
            Session session = null;

            try {
                session = ServiceORM.getSessionFactory().openSession();
                String hqlQuery = "from RssItem WHERE ID = :id";
                Query query = session.createQuery(hqlQuery).setParameter("id", id);
                return (RssItem) query.uniqueResult();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (session != null && session.isOpen())
                    session.close();
            }
            return null;
        };

        item = rssExecutor.getItem(task);
        return item;
    }

    @Override
    public List<RssItem> getAllItemsWithId(Long feed_id) {
        List<RssItem> rssItems;
        RssExecutor rssExecutor = new RssExecutor();

        Callable<List<RssItem>> task = () -> {
            List<RssItem> result = new ArrayList<>();
            Session session = null;

            try {
                session = ServiceORM.getSessionFactory().openSession();
                String hql = "FROM RssItem WHERE FEED_ID = :feed_id";
                Query query = session.createQuery(hql);
                query.setParameter("feed_id", feed_id);
                result = query.list();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (session != null && session.isOpen())
                    session.close();
            }
            return result;
        };

        rssItems = rssExecutor.getItems(task);
        return rssItems;
    }

    @Override
    public List<RssItem> getByLink(URL link) {
        RssExecutor rssExecutor = new RssExecutor();
        List<RssItem> items;
        Callable<List<RssItem>> task = () -> {
            Session session = null;

            try {
                session = ServiceORM.getSessionFactory().openSession();
                String hqlQuery = "FROM RssItem WHERE LINK = :link";
                Criteria criteria = session.createCriteria(RssItem.class, "link");
                return criteria.list();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (session != null && session.isOpen())
                    session.close();
            }
            return null;
        };
        items = rssExecutor.getItems(task);
        return items;
    }
    //    cделать отображение только 5-ти слов

    @Override
    public Map<String, Integer> getTopWords(Long item_id) {
        RssExecutor rssExecutor = new RssExecutor();
        Map<String, Integer> result;
        Callable<Map<String, Integer>> task = () -> {
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

            SortedMap<String, Integer> sortedMap = new TreeMap<>();
            sortedMap.putAll(frequency);
            return sortedMap;
        };

        result = rssExecutor.getMap(task);
        return result;
    }

    @Override
    public Map<String, URL> getAllSourcesRss() {
        List<RssItem> collection = getAllItems();
        Map<String, URL> result = new HashMap<>();

        //System.out.println(collection);
        collection.stream().forEach(rssItem -> {
            result.put(rssItem.getTitle(), rssItem.getLink());
        });
        System.out.println(result);
        return result;
    }
}
