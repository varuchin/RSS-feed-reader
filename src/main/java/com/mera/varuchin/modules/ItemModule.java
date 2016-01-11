package com.mera.varuchin.modules;

import com.mera.varuchin.dao.RssItemDAOImpl;
import com.mera.varuchin.factories.ItemDaoFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ItemModule extends AbstractBinder {

    @Override
    protected void configure() {
        bindFactory(ItemDaoFactory.class)
                .to(RssItemDAOImpl.class);
    }
}
