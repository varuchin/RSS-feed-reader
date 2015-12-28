package com.mera.varuchin;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.modules.HibernateModule;
import com.mera.varuchin.rss.RssFeed;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class Refresher implements Runnable {

    Injector injector = Guice.createInjector(new HibernateModule());
    SessionProvider sessionProvider = injector.getInstance(SessionProvider.class);

    @Override
    public void run() {
        try (Session session = sessionProvider.openSession()) {
            Criteria criteria = session.createCriteria(RssFeed.class);
            List<RssFeed> feedInstances = criteria.list();

            System.out.println(feedInstances);
            if (feedInstances != null) {
                feedInstances.stream()
                        .forEach(instance -> {
                            ZonedDateTime currentTime = ZonedDateTime.now();

                            long timeDifference = ChronoUnit.MINUTES
                                    .between(instance.getModificationTime(), currentTime);

                            if (timeDifference >= 20) {
                                RssFeedDAOImpl rssFeedDAO = new RssFeedDAOImpl();
                                rssFeedDAO.refresh(instance);
                            }
                        });
            }
        }
    }
}
