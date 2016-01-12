import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.dao.RssItemDAOImpl;
import com.mera.varuchin.exceptions.DataBaseFeedException;
import com.mera.varuchin.exceptions.FeedNotFoundException;
import com.mera.varuchin.exceptions.ItemsNotFoundException;
import com.mera.varuchin.exceptions.NoFeedsFoundException;
import com.mera.varuchin.info.FeedInfo;
import com.mera.varuchin.info.ItemInfo;
import com.mera.varuchin.modules.FeedModule;
import com.mera.varuchin.modules.ItemModule;
import com.mera.varuchin.resources.FeedResource;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import junit.framework.Assert;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

public class Rest extends JerseyTest {

    private FeedResource resource;
    private RssFeedDAOImpl feedDAO;
    private RssItemDAOImpl itemDAO;

    @Override
    protected Application configure() {
        feedDAO = mock(RssFeedDAOImpl.class);
        itemDAO = mock(RssItemDAOImpl.class);

        //resource = new FeedResource();
        //resource.setDAO(feedDAO, itemDAO);

        return new ResourceConfig(FeedResource.class).register(new FeedModule())
                .register(new ItemModule())
                .packages("com.mera.varuchin").register(MultiPartFeature.class);
    }

    @Test
    public void testGetExistingItems() throws MalformedURLException {
        List<ItemInfo> infos = new ArrayList<>();
        List<RssItem> items = new ArrayList<>();

        ItemInfo firstInfo = new ItemInfo();
        ItemInfo secondInfo = new ItemInfo();
        firstInfo.setInfo("Title", "Description", "pub_date", "http://link.ru");
        secondInfo.setInfo("Title2", "Description2", "pub_date2", "http://link2.ru");

        RssItem firstItem = new RssItem("Title", "Description", "pub_date",
                new URL("http://link.ru"));
        RssItem secondItem = new RssItem("Title2", "Description2", "pub_date2",
                new URL("http://link2.ru"));

        infos.add(firstInfo);
        infos.add(secondInfo);
        items.add(firstItem);
        items.add(secondItem);

        when(itemDAO.getItems(null, null)).thenReturn(items);

        List<ItemInfo> result = resource.getItems(null, null);

        verify(itemDAO).getItems(null, null);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 2);
    }

    @Test(expected = ItemsNotFoundException.class)
    public void testThrowingItemsNotFoundException() {
        when(itemDAO.getItems(null, null)).thenReturn(new ArrayList<>());
        List<ItemInfo> result = resource.getItems(null, null);

        verify(itemDAO).getItems(null, null);
        Assert.assertTrue(result.size() == 0);
    }

    @Test
    public void testGetExistingFeeds() throws MalformedURLException {
        List<FeedInfo> infos = new ArrayList<>();
        List<RssFeed> feeds = new ArrayList<>();

        FeedInfo firstInfo = new FeedInfo();
        FeedInfo secondInfo = new FeedInfo();
        firstInfo.setInfo("FirstFeed", "http://feeds.bbci.co.uk/news/politics/rss.xml");
        secondInfo.setInfo("SecondFeed", "http://feeds.bbci.co.uk/news/politics/rss.xml");

        RssFeed firstFeed = new RssFeed("FirstFeed",
                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));
        RssFeed secondFeed = new RssFeed("SecondFeed",
                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));

        infos.add(firstInfo);
        infos.add(secondInfo);
        feeds.add(firstFeed);
        feeds.add(secondFeed);

        when(feedDAO.getFeeds(null, null, null)).thenReturn(feeds);

        List<FeedInfo> result = resource.getFeeds(null, null, null);

        verify(feedDAO).getFeeds(null, null, null);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 2);
    }

    @Test(expected = NoFeedsFoundException.class)
    public void testThrowingNoFeedsFoundException() {
        when(feedDAO.getFeeds(null, null, null)).thenReturn(new ArrayList<>());
        List<FeedInfo> result = resource.getFeeds(null, null, null);

        verify(feedDAO).getFeeds(null, null, null);
        Assert.assertTrue(result.size() == 0);
    }

    @Test
    public void testTopWordsMethod() {
        ItemInfo firstWord = mock(ItemInfo.class);
        ItemInfo secondWord = mock(ItemInfo.class);
        ItemInfo thirdWord = mock(ItemInfo.class);

        Map<String, Integer> words = new TreeMap<>();

        words.put("first", 1);
        words.put("second", 2);
        words.put("often", 8);

        firstWord.setWord("first");
        secondWord.setWord("second");
        thirdWord.setWord("often");

        when(itemDAO.getTopWords(1L)).thenReturn(words);

        List<ItemInfo> infos = new ArrayList<>();
        infos.add(thirdWord);
        infos.add(secondWord);
        infos.add(firstWord);

        List<ItemInfo> result = resource.getTopWords(1L);

        verify(itemDAO).getTopWords(1L);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.size() == 3);
        Assert.assertTrue(result.size() == infos.size());
    }

    @Test
    public void testAddNewFeed() throws MalformedURLException {
        URL url = new URL("http://feeds.bbci.co.uk/news/politics/rss.xml");
        RssFeed rssFeed = new RssFeed("FirstFeed",
                url);
        when(feedDAO.getByLink(url)).thenReturn(null);
        doNothing().when(feedDAO).add(rssFeed);
        Response expected = Response.status(Response.Status.CREATED).build();
        Response result = resource.add(rssFeed);

        verify(feedDAO).getByLink(url);
        verify(feedDAO).add(rssFeed);
        Assert.assertNotNull(result);
        Assert.assertEquals(expected.getStatus(), result.getStatus());
    }

    @Test(expected = DataBaseFeedException.class)
    public void testAddExistingFeed() throws MalformedURLException {
        URL url = new URL("http://feeds.bbci.co.uk/news/politics/rss.xml");
        RssFeed rssFeed = new RssFeed("FirstFeed",
                url);
        when(feedDAO.getByLink(url)).thenReturn(rssFeed);

        resource.add(rssFeed);
        verify(feedDAO.getByLink(url));
    }

    @Test
    public void testUpdateExistingFeed() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("FirstFeed",
                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));

        when(feedDAO.getById(1L)).thenReturn(rssFeed);
        doNothing().when(feedDAO).update(rssFeed);

        Response expected = Response.status(Response.Status.OK).build();
        Response result = resource.update(1L, rssFeed);

        verify(feedDAO).getById(1L);
        verify(feedDAO).update(rssFeed);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getStatus() == 200);
        Assert.assertTrue(expected.getStatus() == result.getStatus());
    }

    @Test(expected = FeedNotFoundException.class)
    public void testUpdateNonExistentFeed() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TestFeed",
                new URL("http://test.xml"));

        when(feedDAO.getById(1L)).thenReturn(null);
        resource.update(1L, rssFeed);

        verify(feedDAO.getById(1L));
    }

    @Test
    public void testDeleteExistingFeed() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TestFeed",
                new URL("http://test.xml"));
        when(feedDAO.getById(1L)).thenReturn(rssFeed);

        Response expected = Response.status(Response.Status.OK).build();
        Response result = resource.remove(1L);

        verify(feedDAO).getById(1L);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getStatus() == 200);
        Assert.assertEquals(expected.getStatus(), result.getStatus());
    }

    @Test(expected = FeedNotFoundException.class)
    public void testDeleteNonExistentFeed() throws MalformedURLException {
        when(feedDAO.getById(1L)).thenReturn(null);

        resource.remove(1L);
        verify(feedDAO).getById(1L);
    }

    @Test
    public void testGetByExistingSource() throws MalformedURLException {
        RssItem rssItem = new RssItem("Title", "Description", "Pub_Date",
                new URL("https://test.com"));
        when(feedDAO.getBySource(1L, 2L)).thenReturn(rssItem);

        ItemInfo expected = new ItemInfo();
        expected.setInfo(rssItem);
        ItemInfo result = resource.getBySource(1L, 2L);

        verify(feedDAO).getBySource(1L, 2L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getDescription(), expected.getDescription());
        Assert.assertEquals(result.getFeedLink(), expected.getFeedLink());
        Assert.assertEquals(result.getPub_date(), expected.getPub_date());
        Assert.assertEquals(result.getFeedLink(), expected.getFeedLink());
    }

    @Test(expected = ItemsNotFoundException.class)
    public void testGetByNonExistentSource() {
        when(feedDAO.getBySource(1L, 2L)).thenReturn(null);

        resource.getBySource(1L, 2L);
        verify(feedDAO).getBySource(1L, 2L);
    }


//    @Test
//    public void testMultipartMethod() throws MalformedURLException {
//        List<RssFeed> feeds = new ArrayList<>();
//        FeedParser mockedParser = mock(FeedParser.class);
//        InputStream mockedDocument = mock(InputStream.class);
//
//        RssFeed firstFeed = new RssFeed("FirstFeed",
//                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));
//        RssFeed secondFeed = new RssFeed("SecondFeed",
//                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));
//
//        feeds.add(firstFeed);
//        feeds.add(secondFeed);
//
//        when(mockedParser.parseFeeds(mockedDocument)).thenReturn(feeds);
//
//        feeds.stream().forEach(feed->{
//            when(feed)
//        });
//    }
}