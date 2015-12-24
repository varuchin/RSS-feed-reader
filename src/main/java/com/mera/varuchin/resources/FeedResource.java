package com.mera.varuchin.resources;


import com.mera.varuchin.dao.RSSfeedDAO;
import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.dao.RssItemDAOImpl;
import com.mera.varuchin.info.FeedInfo;
import com.mera.varuchin.info.ItemInfo;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/rss")
@Produces(MediaType.APPLICATION_JSON)
public class FeedResource {

    private RSSfeedDAO dao;

    private RssItemDAO itemDAO = new RssItemDAOImpl();

    public FeedResource() {
        dao = new RssFeedDAOImpl();
    }

    @GET
    @Path("/items")
    public List<ItemInfo> getItems(@QueryParam("page") Integer page,
                                   @QueryParam("pageSize") Integer pageSize) {
        List<RssItem> items = itemDAO.getItems(page, pageSize);
        ItemInfo information = new ItemInfo();
        List<ItemInfo> result = information.setItemListInfo(items);

        return result;
    }

    @GET
    @Path("/feeds")
    public List<FeedInfo> getFeeds(@QueryParam("page") Integer page,
                                   @QueryParam("pageSize") Integer papeSize,
                                   @QueryParam("name") String name) {
        List<RssFeed> feeds = dao.getFeeds(page, papeSize, name);
        FeedInfo feedInfo = new FeedInfo();
        List<FeedInfo> information = feedInfo.setFeedListInfo(feeds);

        return information;
    }

    @GET
    @Path("items/{id}/words")
    public List<ItemInfo> getTopWords(@PathParam("id") Long id) {
        ItemInfo itemInfo = new ItemInfo();
        List<ItemInfo> information = itemInfo.topWords(itemDAO.getTopWords(id));

        return information;
    }

//    @GET
//    @Path("/feeds")
//    public List<Wrapper> getAllFeeds(){
//        List<RssFeed> feeds = dao.getAllRegisteredFeeds();
//        Wrapper wrapper = new Wrapper();
//        List<Wrapper> wrappers = wrapper.wrapFeedList(feeds);
//        return wrappers;
//    }

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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/feeds/{id}")
    public Response update(@PathParam("id") Long id, RssFeed rssFeed) {
        RssFeed originRssFeed = new RssFeedDAOImpl().getById(id);

        if (originRssFeed == null) {
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
    @Path("/feeds/{id}")
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
    @Path("/feeds/{feed_id}/items/{item_id}")
    public ItemInfo getBySource(@PathParam("feed_id") Long feed_id,
                                @PathParam("item_id") Long item_id) {
        RssItem item = dao.getBySource(feed_id, item_id);
        ItemInfo information = new ItemInfo();
        information.setInfo(item);
        return information;
    }

//    @POST
//    @Path("feeds/upload")
//    @Produces("text/xml")
//    public Response addFeed(@Multipart(value = "sources", type = "text/xml") ObjectInputStream inputStream) {
//        dao.parseSources(inputStream);
//        return Response.status(Response.Status.CREATED).build();
//    }
}
