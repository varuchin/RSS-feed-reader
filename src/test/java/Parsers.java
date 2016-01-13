import com.mera.varuchin.parsers.FeedParser;
import com.mera.varuchin.parsers.ItemParser;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Parsers {
    private URL url;
    private InputStream itemsStream = null;
    private InputStream feedsStream = null;

    @Before
    public void initialize() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            url = new URL("http://feeds.bbci.co.uk/news/uk/rss.xml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpHost proxy = new HttpHost("proxy.merann.ru", 8080, "http");
            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();
            HttpGet httpGet = new HttpGet(url.toString());
            httpGet.setConfig(config);

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            itemsStream = httpEntity.getContent();

            InputStream mockedStream = Mockito.mock(InputStream.class);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParsingItemsMethod() throws MalformedURLException {
        ItemParser parser = new ItemParser();
        List<RssItem> items;

        items = parser.parseItems(itemsStream);

        Assert.assertTrue(items.size() != 0);
        Assert.assertNotNull(items);
    }

    @Test
    public void testParsingFeedsMethod() throws MalformedURLException, FileNotFoundException {
        List<RssFeed> expectedFeedList = new ArrayList<>();
        List<RssFeed> actualFeedList;
        FeedParser parser = new FeedParser();

        RssFeed firstFeed = new RssFeed("TEST1",
                new URL("http://feeds.bbci.co.uk/news/science_and_environment/rss.xml"));
        RssFeed secondFeed = new RssFeed("TEST2",
                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));
        RssFeed thirdFeed = new RssFeed("TEST3",
                new URL("http://feeds.bbci.co.uk/news/business/rss.xml"));

        expectedFeedList.add(firstFeed);
        expectedFeedList.add(secondFeed);
        expectedFeedList.add(thirdFeed);

        feedsStream = new FileInputStream
                ("C:\\Users\\varuchin.MERA\\Desktop\\DOCUMENT.xml");

        actualFeedList = parser.parseFeeds(feedsStream);

        Assert.assertNotNull(actualFeedList);
        Assert.assertEquals(actualFeedList.size(), expectedFeedList.size());

        IntStream.range(0, 3).forEach(index -> {

            Assert.assertTrue(expectedFeedList.get(index).getName()
                    .equals(actualFeedList.get(index).getName()));

            Assert.assertTrue(expectedFeedList.get(index).getLink().toString()
                    .equals(actualFeedList.get(index).getLink().toString()));
        });


    }
}
