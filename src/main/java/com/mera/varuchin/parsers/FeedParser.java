package com.mera.varuchin.parsers;

import com.mera.varuchin.rss.RssExecutor;
import com.mera.varuchin.rss.RssFeed;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

public class FeedParser {

    public FeedParser(){}

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

                        RssFeed rssFeed = null;
                        try {
                            rssFeed = new RssFeed(name, new URL(link));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        rssFeeds.add(rssFeed);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rssFeeds;
        };

        return rssExecutor.getFeeds(task);
    }
}
