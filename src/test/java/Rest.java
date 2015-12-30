import com.mera.varuchin.info.FeedInfo;
import com.mera.varuchin.resources.FeedResource;
import com.mera.varuchin.rss.RssFeed;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Rest extends JerseyTest {

    @Override
    protected javax.ws.rs.core.Application configure() {
        return new ResourceConfig(FeedResource.class).register(MultiPartFeature.class);
    }

    @Test
    public void testPostQuery() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TEST_NAME",
                new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));
        Response response = target("/rss/feeds").request()
                .post(Entity.entity(rssFeed, MediaType.APPLICATION_JSON));

        Assert.assertNotNull(response);
        Assert.assertTrue(response.getStatus() == 201);
    }

    @Test
    public void testGetAllFeedsQuery() throws MalformedURLException {
        RssFeed firstFeed = new RssFeed("TEST_NAME1",
                new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));

        RssFeed secondFeed = new RssFeed("TEST_NAME2",
                new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));

        RssFeed thirdFeed = new RssFeed("TEST_NAME3",
                new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));

        target("/rss/feeds").request()
                .post(Entity.entity(firstFeed, MediaType.APPLICATION_JSON));
        target("/rss/feeds").request()
                .post(Entity.entity(secondFeed, MediaType.APPLICATION_JSON));
        target("/rss/feeds").request()
                .post(Entity.entity(thirdFeed, MediaType.APPLICATION_JSON));


        FeedInfo info1 = new FeedInfo(firstFeed);
        FeedInfo info2 = new FeedInfo(secondFeed);
        FeedInfo info3 = new FeedInfo(thirdFeed);

        List<RssFeed> feeds = target("rss/feeds").request()
                .get(new GenericType<List<RssFeed>>() {
                });

        System.out.println(feeds.get(0));
//        System.out.println(response.getStatus());
//        System.out.println(response.getEntity());
        System.err.println("");
    }

}
