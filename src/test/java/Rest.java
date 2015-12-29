import com.mera.varuchin.info.FeedInfo;
import com.mera.varuchin.resources.FeedResource;
import com.mera.varuchin.rss.RssFeed;
import junit.framework.Assert;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
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
        target("/rss/feeds").request()
                .post(Entity.entity(rssFeed, MediaType.APPLICATION_JSON));

        List<FeedInfo> feedList = target("/rss/feeds").request()
                .get(new GenericType<List<FeedInfo>>() {
                });

        System.err.println(feedList);
        Assert.assertEquals(1, feedList.size());
    }

}
