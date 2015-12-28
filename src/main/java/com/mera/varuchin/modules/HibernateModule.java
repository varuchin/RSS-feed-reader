package com.mera.varuchin.modules;


import com.google.inject.AbstractModule;
import com.mera.varuchin.SessionProvider;

public class HibernateModule extends AbstractModule{

    @Override
    public void configure(){
        bind(SessionProvider.class);
    }
}
