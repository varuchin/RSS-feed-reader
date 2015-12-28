package com.mera.varuchin.modules;

import com.google.inject.AbstractModule;
import com.mera.varuchin.dao.RssFeedDAO;
import com.mera.varuchin.dao.RssFeedDAOImpl;


public class FeedModule extends AbstractModule {

    @Override
    protected void configure(){
        bind(RssFeedDAO.class).to(RssFeedDAOImpl.class);
    }

}
