import com.mera.varuchin.parsers.FeedParser;
import com.mera.varuchin.parsers.ItemParser;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Parsers {
    private InputStream itemsStream = null;
    private InputStream feedsStream = null;

    @Before
    public void initialize() {
        String itemsTestInput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<?xml-stylesheet title=\"XSL_formatting\" type=\"text/xsl\" href=\"/shared/bsp/xsl/rss/nolsol.xsl\"?>\n" +
                "\n" +
                "<rss xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:atom=\"http://www.w3.org/2005/Atom\" version=\"2.0\">  \n" +
                "  <channel> \n" +
                "    <title>BBC News - Home</title>  \n" +
                "    <link>http://www.bbc.co.uk/news/#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>  \n" +
                "    <description>The latest stories from the Home section of the BBC News web site.</description>  \n" +
                "    <language>en-gb</language>  \n" +
                "    <lastBuildDate>Thu, 14 Jan 2016 08:16:11 GMT</lastBuildDate>  \n" +
                "    <copyright>Copyright: (C) British Broadcasting Corporation, see http://news.bbc.co.uk/2/hi/help/rss/4498287.stm for terms and conditions of reuse.</copyright>  \n" +
                "    <image> \n" +
                "      <url>http://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif</url>  \n" +
                "      <title>BBC News - Home</title>  \n" +
                "      <link>http://www.bbc.co.uk/news/#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>  \n" +
                "      <width>120</width>  \n" +
                "      <height>60</height> \n" +
                "    </image>  \n" +
                "    <ttl>15</ttl>  \n" +
                "    <atom:link href=\"https://feeds.bbci.co.uk/news/rss.xml\" rel=\"self\" type=\"application/rss+xml\"/>  \n" +
                "    <item> \n" +
                "      <title>test</title>  \n" +
                "      <description>test</description>  \n" +
                "      <link>https://test.com</link>  \n" +
                "      <pubDate>Wed, 13 Jan 2016 13:32:06 GMT</pubDate> \n" +
                "    </item> " +
                "</channel> \n" +
                "</rss>";

        String feedTestInput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sources>\n" +
                "  <source>\n" +
                "     <name>TEST1</name>\n" +
                "     <link>http://feeds.bbci.co.uk/news/science_and_environment/rss.xml</link>\n" +
                " </source>\n" +
                "<source>\n" +
                "<name>TEST2</name>\n" +
                "<link>http://feeds.bbci.co.uk/news/politics/rss.xml</link>\n" +
                "</source>\n" +
                "<source>\n" +
                "<name>TEST3</name>\n" +
                "<link>http://feeds.bbci.co.uk/news/business/rss.xml\n" +
                "</link>\n" +
                "</source>\n" +
                "</sources>";

        itemsStream = new ByteArrayInputStream(
                itemsTestInput.getBytes(StandardCharsets.UTF_8));
        feedsStream = new ByteArrayInputStream(feedTestInput
                .getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testParsingItemsMethod() throws MalformedURLException {
        ItemParser parser = new ItemParser();
        List<RssItem> items;

        items = parser.parseItems(itemsStream);

        Assert.assertNotNull(items);
        Assert.assertTrue(items.size() != 0);
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getTitle(), "test");
        Assert.assertEquals(items.get(0).getDescription(), "test");
        Assert.assertEquals(items.get(0).getLink().toString(), "https://test.com");
        Assert.assertEquals(items.get(0).getPubDate(), "Wed Jan 13 16:32:06 MSK 2016");
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
