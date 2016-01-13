package com.mera.varuchin;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionProvider {
    private static volatile SessionFactory sessionFactory = setUp();

    public SessionProvider(){}
    protected static SessionFactory setUp() {

        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .configure().build();
        try {
            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }

        return null;
    }

    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (SessionFactory.class) {
                if (sessionFactory == null) {
                    sessionFactory = setUp();
                }
            }
        }

        return sessionFactory;
    }

    public Session openSession() {
        return getSessionFactory().openSession();
    }
}