package com.mera.varuchin.modules;

import com.google.inject.AbstractModule;
import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.dao.RssItemDAOImpl;

public class ItemModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RssItemDAO.class).to(RssItemDAOImpl.class);
    }
}
