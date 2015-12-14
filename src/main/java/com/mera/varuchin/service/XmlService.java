package com.mera.varuchin.service;

import com.mera.varuchin.rss.RssItem;
import com.thoughtworks.xstream.XStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class XmlService {

    public static void writeXmlFile(Collection<RssItem> list) {
        try {
            DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("Item");
            document.appendChild(root);

            Element source = document.createElement("Source");
            root.appendChild(source);


            list.stream().forEach(rssItem -> {
                Element title = document.createElement("title");
                title.appendChild(document.createTextNode(String
                        .valueOf(rssItem.getTitle())));
                source.appendChild(title);

                Element description = document.createElement("description");
                description.appendChild(document.createTextNode(String
                        .valueOf(rssItem.getDescription())));
                source.appendChild(description);

                Element pubDate = document.createElement("pubDate");
                pubDate.appendChild(document.createTextNode(String
                        .valueOf(rssItem.getPubDate())));
                source.appendChild(pubDate);

                Element link = document.createElement("link");
                link.appendChild(document.createTextNode(String
                        .valueOf(rssItem.getLink())));
                source.appendChild(link);
            });

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);
            try {
                FileWriter fos = new FileWriter("C:\\Users\\varuchin.MERA\\Desktop\\XMLs/ros.xml");
                StreamResult result = new StreamResult(fos);
                transformer.transform(domSource, result);

            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (TransformerException ex) {
            System.out.println("Error outputting document");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Collection<String> buildXML(Collection<RssItem> rssItems){
        XStream xStream = new XStream();
        Collection<String> result = new ArrayList<>();
        rssItems.stream().forEach(rssItem -> {
            String temp = xStream.toXML(rssItem);
            result.add(temp);
        });
        return result;
    }
}
