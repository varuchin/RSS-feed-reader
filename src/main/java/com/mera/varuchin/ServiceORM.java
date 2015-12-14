package com.mera.varuchin;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


public class ServiceORM {
    private static SessionFactory sessionFactory = setUp();

    protected static SessionFactory setUp() {

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        System.err.println("|NULL");
        return null;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            setUp();
        }
        return sessionFactory;
    }
}