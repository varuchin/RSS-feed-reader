import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mera.varuchin.SessionProvider;
import com.mera.varuchin.dao.RssFeedDAO;
import com.mera.varuchin.modules.FeedModule;
import com.mera.varuchin.modules.HibernateModule;
import com.mera.varuchin.rss.RssFeed;
import junit.framework.Assert;
import org.glassfish.jersey.test.JerseyTest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class FeedDAO extends JerseyTest{
    private RssFeedDAO dao = null;
    private SessionFactory sessionFactory;
    private SessionProvider sessionProvider = null;
    private Session session = null;
    private Configuration configuration;

    @Before
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        Injector injector = Guice.createInjector(new FeedModule());
        dao = injector.getInstance(RssFeedDAO.class);

        injector = Guice.createInjector(new HibernateModule());
        sessionProvider = injector.getInstance(SessionProvider.class);
        session = sessionProvider.openSession();
//        configuration = new Configuration();
//        configuration.configure(new File("hibernate.cfg.xml"));
//        sessionFactory = configuration.buildSessionFactory();
//        session = sessionFactory.openSession();
    }

    @After
    public void tearDown() {
//        try {
//            session.getTransaction().rollback();
//        }
//        catch (NullPointerException e){
//            e.printStackTrace();
//        }
        if (session != null && session.isOpen())
            session.close();
    }

    @Test
    public void testAddFeed() throws MalformedURLException {
        String feedName = "TEST_FEED";
        URL feedURL = new URL("http://feeds.bbci.co.uk/news/technology/rss.xml");
        RssFeed rssFeed = new RssFeed(feedName, feedURL);

        dao.add(rssFeed);
        rssFeed = dao.getByLink(feedURL);
        Assert.assertNotNull(rssFeed);
        Assert.assertEquals(rssFeed.getLink().toString(),
                "http://feeds.bbci.co.uk/news/technology/rss.xml");
        Assert.assertEquals(rssFeed.getName(), feedName);
    }

    @Test
    public void testRemoveFeed() throws MalformedURLException {
        String feedName = "TEST_FEED";
        URL feedURL = new URL("http://feeds.bbci.co.uk/news/technology/rss.xml");
        RssFeed rssFeed = new RssFeed(feedName, feedURL);

        dao.add(rssFeed);
        rssFeed = dao.getByLink(feedURL);
        dao.remove(rssFeed.getId());

        Assert.assertNull(dao.getByLink(feedURL));
    }

    @Test
    public void testGetById() throws MalformedURLException {
        String feedName = "TEST_FEED";
        URL feedURL = new URL("http://feeds.bbci.co.uk/news/technology/rss.xml");
        RssFeed rssFeed = new RssFeed(feedName, feedURL);
        RssFeed expectedFeed = null;

        dao.add(rssFeed);
        rssFeed = dao.getByLink(feedURL);
        expectedFeed = dao.getById(rssFeed.getId());

        Assert.assertEquals(rssFeed, expectedFeed);
    }
}
