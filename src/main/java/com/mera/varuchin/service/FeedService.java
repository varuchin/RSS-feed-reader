package com.mera.varuchin.service;


import com.mera.varuchin.dao.RSSfeedDAO;
import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

@Path("/rss")
@Produces(MediaType.APPLICATION_JSON)
public class FeedService {

    private RSSfeedDAO dao;

    public FeedService() {
        dao = new RssFeedDAOImpl();
    }

    @GET
    @Path("/links")
    public Collection<RssItem> getBySource(@QueryParam("url") URL url){
        return dao.getNewsFromSource(url);
    }

    @GET
    @Path("/feeds/{page}/{pageSize}/{name}")
    public Collection<RssFeed> getByName(@PathParam("page") int page,
                                         @PathParam("pageSize") int pageSize,
                                         @QueryParam("name") String name) {

        return dao.getFeedsByName(page, pageSize, name);
    }

    @GET
    @Path("/feeds/{page}/{pageSize}")
    public ArrayList<RssFeed> listed(@PathParam("page")
                                     int page, @PathParam("pageSize") int pageSize) {
        return dao.getAllListed(page, pageSize);
    }

    @POST
    @Path("/feeds")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(RssFeed rssFeed) {
        System.out.println(rssFeed);
        System.out.println(rssFeed.getLink());
        RssFeed rssFeed1 = dao.getByLink(rssFeed.getLink());
        System.out.println(rssFeed1);
        if (dao.getByLink(rssFeed.getLink()) == null) {

            dao.add(rssFeed);
            URI location = URI.create("/rss/feeds" + rssFeed.getId());
            return Response.created(location).build();

        } else {
            System.err.println("Such feed is already in the DB");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, RssFeed rssFeed) {
        RssFeed originRssFeed = new RssFeedDAOImpl().getById(id);

        System.err.println(rssFeed.getLink());
        if (dao.getById(id) == null) {
            System.err.println("Nothing to update: no such element by this ID.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            System.err.print("RSS Feed was found.");
            originRssFeed.setName(rssFeed.getName());
            originRssFeed.setLink(rssFeed.getLink());

            dao.update(originRssFeed);
            System.err.println("Updated.");
            return Response.ok().build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Long id) {
        RssFeed originRssFeed = dao.getById(id);
        if (originRssFeed == null) {
            System.err.println("RSS item with such ID is not found.");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        dao.remove(id);
        return Response.ok().build();
    }

    @GET
    @Path("/feeds")
    public Collection<RssFeed> getAll() {
        System.err.println("All");
        return dao.getAllRegisteredFeeds();
    }

    @GET
    @Path("/feeds/by")
    public Collection<RssFeed> getByName(@QueryParam("name") String name) {
        return dao.getRssSortedByName(name);
    }

    @GET
    @Path("/feeds/{id}")
    public Collection<RssItem> getItems(@PathParam("id") Long id) {
        RssFeed rssFeed = new RssFeedDAOImpl().getById(id);
        if (rssFeed.equals(null)) {
            System.err.println("No feed with such ID");
            return null;
        }

        System.err.println("Found.");
        return dao.getAllItems(id);
    }

    @GET
    @Path("/feeds/source")
    public RssItem getBySource(@QueryParam("title") String title, @QueryParam("link") URL link) {
        return dao.getBySource(title, link);
    }
}
