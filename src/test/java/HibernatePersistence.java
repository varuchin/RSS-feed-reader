import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class HibernatePersistence {

    private SessionFactory sessionFactory;
    private Session session = null;

    @Before
    public void initialize() {
        Configuration configuration = new Configuration().addAnnotatedClass(RssFeed.class)
                .addAnnotatedClass(RssItem.class);
        configuration.setProperty("hibernate.dialect",
                "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class",
                "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @Test
    public void testSaveFunctionORM() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TEST_NAME",
                new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));
        session.beginTransaction();
        session.save(rssFeed);

        RssFeed result;
        Criteria criteria = session.createCriteria(RssFeed.class);
        result = (RssFeed) criteria.uniqueResult();

        Assert.assertNotNull(result);
        Assert.assertTrue(rssFeed.equals(result));
    }

    @Test
    public void testDeleteFunctionORM() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TEST_NAME",
                new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));
        session.beginTransaction();
        session.save(rssFeed);

        session.delete(rssFeed);

        RssFeed result;
        Criteria criteria = session.createCriteria(RssFeed.class);
        result = (RssFeed) criteria.uniqueResult();
        Assert.assertNull(result);
        System.out.println(criteria.list());
        Assert.assertEquals(criteria.list().size(), 0);
    }

    @Test
    public void testUpdateFunctionORM() throws MalformedURLException {
        RssFeed rssFeed = new RssFeed("TEST_NAME",
                new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));
        session.beginTransaction();
        session.save(rssFeed);

        rssFeed.setName("CHANGED_NAME");
        session.update(rssFeed);

        Criteria criteria = session.createCriteria(RssFeed.class);
        RssFeed result = (RssFeed) criteria.uniqueResult();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getName(), rssFeed.getName());
        Assert.assertTrue(result.getName().equals(rssFeed.getName()));
    }

    @After
    public void tearDown() {
        if (session != null && session.isOpen())
            session.close();
        sessionFactory.close();
    }
}
