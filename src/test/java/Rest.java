import com.mera.varuchin.dao.RssFeedDAO;
import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.info.FeedInfo;
import com.mera.varuchin.info.ItemInfo;
import com.mera.varuchin.resources.FeedResource;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import junit.framework.Assert;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class Rest extends JerseyTest {

    @Mock
    RssFeedDAO feedDAO;
    @Mock
    RssItemDAO itemDAO;

    @FormDataParam("file")
    InputStream file;

    private class RssModule extends AbstractBinder {

        @Override
        protected void configure() {
            bind(feedDAO).to(RssFeedDAO.class);
            bind(itemDAO).to(RssItemDAO.class);
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(FeedResource.class)
                .register(new RssModule())
                .packages("com.mera.varuchin.exceptions",
                        "com.mera.varuchin.parsers",
                        "com.mera.varuchin.rss",
                        "com.mera.varuchin.dao,",
                        "com.mera.varuchin.modules",
                        "com.mera.varuchin.factories",
                        "com.mera.varuchin.info",
                        "com.mera.varuchin.parsers",
                        "com.mera.varuchin.SessionProvider")
                .register(MultiPartFeature.class);
    }

    @Test
    public void testGetExistingItems() throws MalformedURLException {
        List<RssItem> items = new ArrayList<>();
        RssItem firstItem = new RssItem("Title", "Description", "pub_date",
                new URL("http://link.ru"));
        RssItem secondItem = new RssItem("Title2", "Description2", "pub_date2",
                new URL("http://link2.ru"));

        items.add(firstItem);
        items.add(secondItem);

        when(itemDAO.getItems(null, null)).thenReturn(items);

        List<ItemInfo> result = target("/rss/items").request()
                .get(new GenericType<List<ItemInfo>>() {
                });

        verify(itemDAO).getItems(null, null);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 2);
    }


    @Test
    public void testGetNonExistentItem() {
        when(itemDAO.getItems(null, null)).thenReturn(new ArrayList<>());

        Response expected = Response.status(Response.Status.NOT_FOUND).build();
        Response result = target("/rss/items").request().get();

        verify(itemDAO).getItems(null, null);
        Assert.assertNotNull(result);
        Assert.assertEquals(expected.getStatus(), result.getStatus());
        Assert.assertEquals(result.getStatus(), 404);
    }


    @Test
    public void testGetExistingFeeds() throws MalformedURLException {
        List<RssFeed> feeds = new ArrayList<>();

        RssFeed firstFeed = new RssFeed("FirstFeed",
                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));
        RssFeed secondFeed = new RssFeed("SecondFeed",
                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));
        feeds.add(firstFeed);
        feeds.add(secondFeed);

        when(feedDAO.getFeeds(null, null, null)).thenReturn(feeds);
        List<FeedInfo> result = target("/rss/feeds")
                .request().get(new GenericType<List<FeedInfo>>() {
                });

        verify(feedDAO).getFeeds(null, null, null);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 2);
    }

    @Test
    public void testThrowingNoFeedsFoundException() {
        when(feedDAO.getFeeds(null, null, null)).thenReturn(new ArrayList<>());

        Response expected = Response.status(Response.Status.NOT_FOUND).build();
        Response result = target("/rss/feeds").request().get();

        verify(feedDAO).getFeeds(null, null, null);
        Assert.assertNotNull(result);
        Assert.assertEquals(expected.getStatus(), result.getStatus());
        Assert.assertEquals(result.getStatus(), 404);
    }


    @Test
    public void testTopWordsMethod() {
        Map<String, Integer> words = new TreeMap<>();

        words.put("first", 1);
        words.put("second", 2);
        words.put("often", 8);

        when(itemDAO.getTopWords(1L)).thenReturn(words);

        List<ItemInfo> result = target("/rss/items/").path("1/words").request()
                .get(new GenericType<List<ItemInfo>>() {
                });

        verify(itemDAO).getTopWords(1L);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.size() == 3);
    }


    @Test
    public void testAddNewFeed() throws MalformedURLException {
        URL url = new URL("http://feeds.bbci.co.uk/news/politics/rss.xml");
        RssFeed rssFeed = new RssFeed("FirstFeed",
                url);

        when(feedDAO.getByLink(url)).thenReturn(null);
        doNothing().when(feedDAO).add(rssFeed);
        Response expected = Response.status(Response.Status.CREATED).build();

        Response result = target("/rss/feeds").request()
                .post(Entity.entity(rssFeed, MediaType.APPLICATION_JSON));

        verify(feedDAO).getByLink(url);
        Assert.assertNotNull(result);
        Assert.assertEquals(expected.getStatus(), result.getStatus());
    }

    @Test
    public void testAddExistingFeed() throws MalformedURLException {
        URL url = new URL("http://feeds.bbci.co.uk/news/politics/rss.xml");
        RssFeed rssFeed = new RssFeed("FirstFeed",
                url);
        when(feedDAO.getByLink(url)).thenReturn(rssFeed);

        Response expected = Response.status(Response.Status.BAD_REQUEST).build();
        Response result = target("/rss/feeds").request()
                .post(Entity.entity(rssFeed, MediaType.APPLICATION_JSON));

        verify(feedDAO).getByLink(url);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getStatus(), expected.getStatus());
        Assert.assertEquals(result.getStatus(), 400);
    }


    @Test
    public void testUpdateExistingFeed() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("FirstFeed",
                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));

        when(feedDAO.getById(1L)).thenReturn(rssFeed);
        doNothing().when(feedDAO).update(rssFeed);

        Response expected = Response.status(Response.Status.OK).build();
        Response result = target("/rss/feeds").path("/1")
                .request().put(Entity.entity(rssFeed, MediaType.APPLICATION_JSON));

        verify(feedDAO).getById(1L);
        verify(feedDAO).update(rssFeed);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getStatus() == 200);
        Assert.assertTrue(expected.getStatus() == result.getStatus());
    }

    @Test
    public void testUpdateNonExistentFeed() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TestFeed",
                new URL("http://test.xml"));

        when(feedDAO.getById(1L)).thenReturn(null);
        Response expected = Response.status(Response.Status.NOT_FOUND).build();
        Response result = target("/rss/feeds").path("/1").request()
                .put(Entity.entity(rssFeed, MediaType.APPLICATION_JSON));

        verify(feedDAO).getById(1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getStatus(), expected.getStatus());
        Assert.assertEquals(result.getStatus(), 404);
    }


    @Test
    public void testDeleteExistingFeed() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TestFeed",
                new URL("http://test.xml"));
        when(feedDAO.getById(1L)).thenReturn(rssFeed);

        Response expected = Response.status(Response.Status.OK).build();
        Response result = target("/rss/feeds").path("/1").request()
                .delete();

        verify(feedDAO).getById(1L);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getStatus() == 200);
        Assert.assertEquals(expected.getStatus(), result.getStatus());
    }


    @Test
    public void testDeleteNonExistentFeed() throws MalformedURLException {
        when(feedDAO.getById(1L)).thenReturn(null);
        Response expected = Response.status(Response.Status.NOT_FOUND).build();
        Response result = target("/rss/feeds").path("/1").request().delete();

        verify(feedDAO).getById(1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getStatus(), expected.getStatus());
        Assert.assertEquals(result.getStatus(), 404);
    }

    @Test
    public void testGetByExistingSource() throws MalformedURLException {
        RssItem rssItem = new RssItem("Title", "Description", "Pub_Date",
                new URL("https://test.com"));
        when(feedDAO.getBySource(1L, 2L)).thenReturn(rssItem);

        ItemInfo expected = new ItemInfo();
        expected.setInfo(rssItem);
        ItemInfo result = target("/rss/feeds").path("/1/items/2").request()
                .get(new GenericType<ItemInfo>() {
                });

        verify(feedDAO).getBySource(1L, 2L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getDescription(), expected.getDescription());
        Assert.assertEquals(result.getFeedLink(), expected.getFeedLink());
        Assert.assertEquals(result.getPub_date(), expected.getPub_date());
        Assert.assertEquals(result.getFeedLink(), expected.getFeedLink());
    }

    @Test
    public void testGetByNonExistentSource() {
        when(feedDAO.getBySource(1L, 2L)).thenReturn(null);

        Response expected = Response.status(Response.Status.NOT_FOUND).build();
        Response result = target("/rss/feeds").path("/1/items/2").request()
                .get();

        verify(feedDAO).getBySource(1L, 2L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getStatus(), expected.getStatus());
        Assert.assertEquals(result.getStatus(), 404);
    }


    @Test
    public void testMultipartMethod() {
        String input = "--12\n" +
                "Content-Disposition: form-data; name=\"file\"; " +
                "filename=\"file.xml\"\n" +
                "Content-Type: text/xml\n" +
                "\n" +
                "\n" +
                "<sources>\n" +
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
                "</sources>\n" +
                "--12--";

        MultiPart multiPart = new MultiPart();
        multiPart.bodyPart(new BodyPart(input, MediaType.TEXT_XML_TYPE));

        file = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8));
        //multiPart.setEntity(input.getBytes());
        //multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("Content-Type", "multipart/form-data; boundary=12");
        formDataMultiPart.bodyPart(new BodyPart(input, MediaType.TEXT_XML_TYPE));

        //formDataMultiPart.getEntity();
        //  InputStream inputStream1 = multiPart.getEntityAs(InputStream.class);

        Response expected = Response.status(Response.Status.OK).build();
        Response result = target("/rss/feeds").path("/upload").request()
                .post(Entity.entity(file, MediaType.MULTIPART_FORM_DATA));

        Assert.assertNotNull(result);
        Assert.assertEquals(expected.getStatus(), result.getStatus());
        Assert.assertEquals(result.getStatus(), 200);
    }
}