import com.mera.varuchin.info.FeedInfo;
import com.mera.varuchin.info.ItemInfo;
import com.mera.varuchin.resources.FeedResource;
import com.mera.varuchin.rss.RssFeed;
import junit.framework.Assert;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.mock;

public class Rest extends JerseyTest {

    @Override
    protected javax.ws.rs.core.Application configure() {
        return new ResourceConfig(FeedResource.class).packages().register(MultiPartFeature.class);
    }



    @Test
    public void testGetItemsQuery() throws MalformedURLException, InterruptedException {
        RssFeed feed = new RssFeed("Health",
                new URL("http://feeds.bbci.co.uk/news/health/rss.xml"));
        target("/rss/feeds").request()
                .post(Entity.entity(feed, MediaType.APPLICATION_JSON));

        Thread.sleep(300);
        List<ItemInfo> information = target("/rss/items").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<ArrayList<ItemInfo>>() {
                });

        Assert.assertNotNull(information);
    }

    @Ignore
    @Test
    public void testGetTopWordsQuery() throws MalformedURLException, InterruptedException {
        RssFeed feed = new RssFeed("Health",
                new URL("http://feeds.bbci.co.uk/news/health/rss.xml"));
        target("/rss/feeds").request()
                .post(Entity.entity(feed, MediaType.APPLICATION_JSON));

        Thread.sleep(300);
        List<ItemInfo> information =
                target("/rss/items/2/words").request(MediaType.APPLICATION_JSON)
                        .get(new GenericType<ArrayList<ItemInfo>>() {
                        });

        Assert.assertNotNull(information);
        Assert.assertEquals(5, information.size());
    }

    @Ignore
    @Test
    public void testPostQuery() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TEST_NAME",
                new URL("http://feeds.bbci.co.uk/news/uk/rss.xml"));
        Response response = target("/rss/feeds").request()
                .post(Entity.entity(rssFeed, MediaType.APPLICATION_JSON));

        Assert.assertNotNull(response);
        Assert.assertTrue(response.getStatus() == 201);
    }

    //не работает
    @Deprecated
    @Ignore
    @Test
    public void testPutQuery() throws MalformedURLException, InterruptedException {
        RssFeed feed = new RssFeed("Health",
                new URL("http://feeds.bbci.co.uk/news/health/rss.xml"));
        RssFeed changedFeed = new RssFeed("ChangedHealth",
                new URL("http://feeds.bbci.co.uk/news/technology/rss.xml"));

        target("/rss/feeds").request()
                .post(Entity.entity(feed, MediaType.APPLICATION_JSON));

        List<FeedInfo> information = target("rss/feeds").request()
                .get(new GenericType<List<FeedInfo>>() {
                });
//        target("/rss/feeds/1").request()
//                .put(Entity.entity(changedFeed, MediaType.APPLICATION_JSON));
//
//       List<FeedInfo> feeds = target("/rss/feeds").request(MediaType.APPLICATION_JSON)
//               .get(new GenericType<List<FeedInfo>>(){});
//        System.out.println(information.size());
    }

    // ???HTTP 500 Internal Server Error???
    @Test
    public void testDeleteQuery() throws MalformedURLException, InterruptedException{
        RssFeed rssFeed = mock(RssFeed.class);
        rssFeed.setLink(new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));
        rssFeed.setName("Politics");

        RssFeed feed = new RssFeed("Politics",
                new URL("http://feeds.bbci.co.uk/news/politics/rss.xml"));
        target("/rss/feeds").request()
                .post(Entity.entity(feed, MediaType.APPLICATION_JSON));

        Thread.sleep(1000);
        List<FeedInfo> beforeDelete = target("/rss/feeds")
                .request(MediaType.APPLICATION_JSON).get(new GenericType<List<FeedInfo>>(){});

        Thread.sleep(1000);
        target("/rss/feeds/1").request().delete();

        List<FeedInfo> afterDelete = target("/rss/feeds")
                .request(MediaType.APPLICATION_JSON).get(new GenericType<List<FeedInfo>>(){});

        Assert.assertTrue(beforeDelete.size() == 1);
        Assert.assertTrue(afterDelete.size() == 0);


    }

    @Ignore
    @Test
    public void testGetAllFeedsQuery() throws MalformedURLException {
        List<RssFeed> feeds = new ArrayList<>();

        RssFeed firstFeed = new RssFeed("TEST_NAME1",
                new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));
        RssFeed secondFeed = new RssFeed("TEST_NAME2",
                new URL("http://feeds.bbci.co.uk/news/education/rss.xml"));
        RssFeed thirdFeed = new RssFeed("TEST_NAME3",
                new URL("http://feeds.bbci.co.uk/news/science_and_environment/rss.xml"));

        feeds.add(firstFeed);
        feeds.add(secondFeed);
        feeds.add(thirdFeed);

        feeds.stream().forEach(feed ->
                target("/rss/feeds").request()
                        .post(Entity.entity(feed, MediaType.APPLICATION_JSON)));

        List<FeedInfo> information = target("rss/feeds").request()
                .get(new GenericType<List<FeedInfo>>() {
                });

        Assert.assertNotNull(feeds);
        Assert.assertEquals(feeds.size(), 3);

        IntStream.range(0, 3).forEach(index -> {

            Assert.assertTrue(feeds.get(index).getName()
                    .equals(information.get(index).getName()));

            Assert.assertTrue(feeds.get(index).getLink().toString()
                    .equals(information.get(index).getFeed_link()));
        });
    }
}
