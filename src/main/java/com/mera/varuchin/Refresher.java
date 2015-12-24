package com.mera.varuchin;


import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.rss.RssFeed;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class Refresher implements Runnable {

    @Override
    public void run() {
        try (Session session = ServiceORM.openSession()) {
            Criteria criteria = session.createCriteria(RssFeed.class);
            List<RssFeed> feedInstances = criteria.list();

            System.out.println(feedInstances);
            if (feedInstances != null) {
                feedInstances.stream()
                        .forEach(instance -> {
                    //ставить ZonedDateTime
                    LocalTime currentTime = LocalTime.now(ZoneId.of("Europe/Moscow"));
                    //смапить в базу modification time
                    //выставлять при сохранении
                    long timeDifference = ChronoUnit.MINUTES
                            .between(instance.getCreationTime(), currentTime);

                    //сделать чтобы стер старые items и загрузил новые
                    //а не пересохранял
                    if (timeDifference >= 1) {
                        RssFeedDAOImpl rssFeedDAO = new RssFeedDAOImpl();
                        rssFeedDAO.refresh(instance);
                    }
                });
            }
        }
    }
}
