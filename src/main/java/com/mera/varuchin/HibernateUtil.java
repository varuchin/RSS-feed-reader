package com.mera.varuchin;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            AnnotationConfiguration aConf = new AnnotationConfiguration()
                    .addAnnotatedClass(com.mera.varuchin.rss.RssItem.class);
            Configuration conf = aConf.configure();
            sessionFactory = conf.buildSessionFactory();
            /**
            sessionFactory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(com.mera.varuchin.rss.RssItem.class)
                    .buildSessionFactory();
             */
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}