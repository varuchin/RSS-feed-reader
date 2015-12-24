package com.mera.varuchin;


import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.rss.RssFeed;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class Refresher implements Runnable {

    @Override
    public void run() {
        try (Session session = SessionProvider.openSession()) {
            Criteria criteria = session.createCriteria(RssFeed.class);
            List<RssFeed> feedInstances = criteria.list();

            System.out.println(feedInstances);
            if (feedInstances != null) {
                feedInstances.stream()
                        .forEach(instance -> {

                            ZonedDateTime currentTime = ZonedDateTime.now();
                            //LocalTime currentTime = LocalTime.now(ZoneId.of("Europe/Berlin"));
                            //смапить в базу modification time
                            //выставлять при сохранении
                            long timeDifference = ChronoUnit.MINUTES
                                    .between(instance.getModificationTime(), currentTime);

                            //сделать чтобы стер старые items и загрузил новые
                            //а не пересохранял
                            if (timeDifference >= 20) {
                                RssFeedDAOImpl rssFeedDAO = new RssFeedDAOImpl();
                                rssFeedDAO.refresh(instance);
                            }
                        });
            }
        }
    }
}
