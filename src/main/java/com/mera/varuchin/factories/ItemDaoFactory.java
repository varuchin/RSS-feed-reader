package com.mera.varuchin.factories;

import com.mera.varuchin.dao.RssItemDAOImpl;
import org.glassfish.hk2.api.Factory;

public class ItemDaoFactory implements Factory<RssItemDAOImpl> {

    @Override
    public RssItemDAOImpl provide() {
        return new RssItemDAOImpl();
    }

    @Override
    public void dispose(RssItemDAOImpl rssItemDAO) {

    }
}
