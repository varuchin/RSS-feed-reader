package com.mera.varuchin.parsers;


import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.rss.RssExecutor;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

public class RssParser {

    public List<RssFeed> parseFeeds(InputStream inputStream) {
        RssExecutor rssExecutor = new RssExecutor();
        List<RssFeed> rssFeeds = new ArrayList<>();

        Callable<List<RssFeed>> task = () -> {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(inputStream);
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
                        rssFeeds.add(rssFeed);
                        //rssFeedDAO.add(rssFeed);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rssFeeds;
        };

        return rssExecutor.getFeeds(task);
    }

    public List<RssItem> parseItems(InputStream inputStream) {
        List<RssItem> items = new ArrayList<>();

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
                    String pubDate = new Date(pubDateElem.getFirstChild()
                            .getTextContent()).toString();

                    URL url = null;
                    try {
                        url = new URL(linkElem.getFirstChild().getNodeValue());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    RssItem rssItem = new RssItem(title, description, pubDate, url);
                    items.add(rssItem);
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}
