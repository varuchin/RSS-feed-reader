package com.mera.varuchin.service;


import com.mera.varuchin.dao.RSSfeedDAO;
import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URL;
import java.util.List;

@Path("/rss")
@Produces(MediaType.APPLICATION_JSON)
public class FeedService {

    private RSSfeedDAO dao;

    public FeedService() {
        dao = new RssFeedDAOImpl();
    }

    //+
    @GET
    @Path("/links")
    public List<RssItem> getBySource(@QueryParam("url") URL url) {
        return dao.getNewsFromSource(url);
    }

    //+
    @GET
    @Path("/feeds/list")
    public List<RssFeed> getByName(@QueryParam("page") int page,
                                   @QueryParam("pageSize") int pageSize,
                                   @QueryParam("name") String name) {

        return dao.getFeedsByName(page, pageSize, name);
    }

    //+
    @POST
    @Path("/feeds")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(RssFeed rssFeed) {
        if (dao.getByLink(rssFeed.getLink()) == null) {
            dao.add(rssFeed);
            URI location = URI.create("/rss/feeds" + rssFeed.getId());
            return Response.created(location).build();
        } else {
            System.err.println("Such feed is already in the DB");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    //+
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@QueryParam("id") Long id, RssFeed rssFeed) {
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

    //+
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

    //+
    @GET
    @Path("/feeds/registered")
    public List<RssFeed> getAll() {
        return dao.getAllRegisteredFeeds();
    }

    //+
    @GET
    @Path("/feeds/{feed_id}/items/{item_id}")
    public String getBySource(@PathParam("feed_id") Long feed_id,
                              @PathParam("item_id") Long item_id) {
        JSONObject jsonObject = new JSONObject(dao.getBySource(feed_id, item_id));
        System.err.println(dao.getBySource(feed_id, item_id));
        return jsonObject.toString();
    }
}
