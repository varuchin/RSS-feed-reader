package com.mera.varuchin;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.inject.Singleton;

@Singleton
public class SessionProvider {
    private static volatile SessionFactory sessionFactory = setUp();

    protected static SessionFactory setUp() {

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();

        try {
            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return null;
    }

    private static synchronized SessionFactory getSessionFactory() {
        SessionFactory localSessionFactory = sessionFactory;
        if (localSessionFactory == null) {
            synchronized (SessionFactory.class) {
                localSessionFactory = sessionFactory;
                if (localSessionFactory == null) {
                    sessionFactory = localSessionFactory = setUp();
                }
            }
        }
        return sessionFactory;
    }

    public Session openSession() {
        return getSessionFactory().openSession();
    }
}