package com.mera.varuchin.modules;

import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.factories.FeedDaoFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;


public class FeedModule extends AbstractBinder {

    @Override
    protected void configure(){
        bindFactory(FeedDaoFactory.class)
                .to(RssFeedDAOImpl.class);
    }

}
