package com.mera.varuchin;


import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.rss.RssFeed;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class Checker implements Runnable {

    @Override
    public void run() {
        Session session = null;

        try {
            session = ServiceORM.getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(RssFeed.class);
            List<RssFeed> feedInstances = criteria.list();

            if (feedInstances != null) {
                feedInstances.stream().forEach(instance -> {
                    LocalTime currentTime = LocalTime.now(ZoneId.of("Europe/Berlin"));
                    long timeDifference = ChronoUnit.MINUTES
                            .between(instance.getCreationTime(), currentTime);

                    if (timeDifference >= 10) {
                        RssFeedDAOImpl rssFeedDAO = new RssFeedDAOImpl();
                        rssFeedDAO.refresh(instance);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(session != null && session.isOpen())
                session.close();
        }
    }
}
