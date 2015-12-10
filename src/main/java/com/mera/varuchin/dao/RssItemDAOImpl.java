package com.mera.varuchin.dao;

import com.mera.varuchin.HibernateUtil;
import com.mera.varuchin.rss.RssItem;
import org.hibernate.Query;
import org.hibernate.Session;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;


public class RssItemDAOImpl implements RssItemDAO {


    @Override
    public void add(RssItem rssItem) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(rssItem);
        session.getTransaction().commit();
        session.close();
    }

    //“≈—“»–ќ¬ј“№
    @Override
    public void remove(Long id) {
        RssItem rssItem = new RssItemDAOImpl().getById(id);
        if(rssItem.equals(null)){
            System.err.println("No such element.");
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(rssItem);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void update(RssItem rssItem) {
        if (rssItem.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        if (!rssItem.equals(null)) {
            String hqlUpdateName =
                    "UPDATE RSS SET RSS.NAME " +
                            "= :newNAME WHERE RSS.ID = :ID";
            String hqlUpdateTitle =
                    "UPDATE RSS SET RSS.TITLE " +
                            "= :newTITLE WHERE RSS.ID = :ID";
            String hqlUpdateDescription =
                    "UPDATE RSS SET RSS.DESCRIPTION " +
                            "= :newDESCRIPTION" + " WHERE RSS.ID = :ID";
            String hqlUpdatePubDate =
                    "UPDATE RSS SET RSS.PUB_DATE " +
                            "= :newPUB_DATE WHERE RSS.ID = :ID";
            String hqlUpdateLink =
                    "UPDATE RSS SET RSS.LINK " +
                            "= :newLink WHERE RSS.ID = :ID";

            if (rssItem.getName() != null)
                session.createQuery(hqlUpdateName).setString("newNAME", rssItem.getName())
                        .setString("ID", rssItem.getId().toString()).executeUpdate();

            if (rssItem.getTitle() != null)
                session.createQuery(hqlUpdateTitle).setString("newTITLE", rssItem.getTitle())
                        .setString("ID", rssItem.getId().toString()).executeUpdate();

            if (rssItem.getDescription() != null)
                session.createQuery(hqlUpdateDescription).setString("newDESCRIPTION",
                        rssItem.getDescription())
                        .setString("ID", rssItem.getId().toString()).executeUpdate();

            if (rssItem.getPubDate() != null)
                session.createQuery(hqlUpdatePubDate).setString("newPUB_DATE", rssItem.getPubDate()
                        .toString()).setString("ID", rssItem.getId().toString()).executeUpdate();

            if (rssItem.getLink() != null)
                session.createQuery(hqlUpdateLink).setString("newLINK", rssItem.getLink().toString())
                        .setString("ID", rssItem.getId().toString()).executeUpdate();

            session.getTransaction().commit();
            session.close();
        }
        else
            session.close();
    }

    @Override
    public void update(RssItem rssItem, String name) {
        if (rssItem.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        String hqlUpdate = "UPDATE RSS SET RSS.NAME = :newName WHERE RSS.NAME = :oldName";

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.createQuery(hqlUpdate).setString("newName", name)
                .setString("oldName", rssItem.getName())
                .executeUpdate();
        session.getTransaction().commit();
        session.close();

    }

    @Override
    public void update(RssItem rssItem, URL url) {
        if (rssItem.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        String hqlUpdate = "UPDATE RSS SET RSS.LINK = :newLink WHERE RSS.LINK = :oldLink";

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.createQuery(hqlUpdate).setString("newLink", url.toString())
                .setString("oldLink", rssItem.getLink().toString())
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    //как сделать иначе в одной сессии?
    @Override
    public void update(RssItem rssItem, String name, URL url) {
        if (rssItem.equals(null)) {
            System.err.println("Not found RssItem!");
            return;
        }
        String hqlUpdateName =
                "UPDATE RSS SET RSS.NAME = :newName WHERE RSS.NAME = :oldName";
        String hqlUpdateLink =
                "UPDATE RSS SET RSS.LINK = :newLink WHERE RSS.LINK = :oldLink";

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.createQuery(hqlUpdateName).setString("newName", name)
                .setString("oldName", rssItem.getName())
                .executeUpdate();
        session.getTransaction().commit();

        session.createQuery(hqlUpdateLink).setString("newLink", url.toString())
                .setString("oldLink", rssItem.getLink().toString())
                .executeUpdate();

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public RssItem getById(long id) {
        RssItem result = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            result = (RssItem) session.get(RssItem.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }

        return result;
    }

    @Override
    public Collection<RssItem> getAllRss(){
        Collection<RssItem> result = new ArrayList<>();
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from RSS");
            result = query.list();
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("ERROR ERROR ERROR");
        }
        finally {
            session.close();
        }
        return result;
    }
    @Override
    public RssItem getByRssSource(RssItem rssItem) {
        return null;
    }

    @Override
    public Collection<String> getTopWords(RssItem rssItem) {
        return null;
    }

    @Override
    public Collection<RssItem> getPaginatedList() {
        return null;
    }
}
