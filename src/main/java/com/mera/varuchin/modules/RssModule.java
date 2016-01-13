package com.mera.varuchin.modules;

import com.mera.varuchin.SessionProvider;
import com.mera.varuchin.dao.RssFeedDAO;
import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.factories.FeedDaoFactory;
import com.mera.varuchin.factories.ItemDaoFactory;
import com.mera.varuchin.factories.SessionProviderFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;


public class RssModule extends AbstractBinder {

    @Override
    protected void configure(){
        bindFactory(FeedDaoFactory.class).to(RssFeedDAO.class);
        bindFactory(ItemDaoFactory.class).to(RssItemDAO.class);
        bindFactory(SessionProviderFactory.class).to(SessionProvider.class);
    }

}
