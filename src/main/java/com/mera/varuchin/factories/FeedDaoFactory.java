package com.mera.varuchin.factories;

import com.mera.varuchin.dao.RssFeedDAOImpl;
import org.glassfish.hk2.api.Factory;

public class FeedDaoFactory implements Factory<RssFeedDAOImpl> {

    @Override
    public RssFeedDAOImpl provide() {
        return new RssFeedDAOImpl();
    }

    @Override
    public void dispose(RssFeedDAOImpl rssFeedDAO) {

    }
}
