package com.mera.varuchin.resources;


import com.mera.varuchin.dao.RssFeedDAO;
import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.dao.RssItemDAOImpl;
import com.mera.varuchin.exceptions.*;
import com.mera.varuchin.info.FeedInfo;
import com.mera.varuchin.info.ItemInfo;
import com.mera.varuchin.parsers.FeedParser;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URI;
import java.util.List;


@Path("/rss")
@Produces(MediaType.APPLICATION_JSON)
public class FeedResource {

    //в один ио
    @Inject
    RssFeedDAO dao;

    @Inject
    RssItemDAO itemDAO;

    public FeedResource() {
    }

    public void setDAO(RssFeedDAOImpl feedDAO, RssItemDAOImpl itemDAO){
        this.dao = feedDAO;
        this.itemDAO = itemDAO;
    }

    @GET
    @Path("/items")
    public List<ItemInfo> getItems(@QueryParam("page") Integer page,
                                   @QueryParam("pageSize") Integer pageSize) {
        List<RssItem> items = itemDAO.getItems(page, pageSize);
        if (items.size() == 0)
            throw new ItemsNotFoundException("No items were found.");

        ItemInfo information = new ItemInfo();
        List<ItemInfo> result = information.setItemListInfo(items);

        return result;
    }

    @GET
    @Path("/feeds")
    public List<FeedInfo> getFeeds(@QueryParam("page") Integer page,
                                   @QueryParam("pageSize") Integer pageSize,
                                   @QueryParam("name") String name) {
        List<RssFeed> feeds = dao.getFeeds(page, pageSize, name);
        if (feeds.size() == 0)
            throw new NoFeedsFoundException("No feeds are found in DB.");

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
            FeedInfo feedInfo = new FeedInfo();
            feedInfo.setInfo(rssFeed);
            System.err.println("Such feed is already in the DB");
            throw new DataBaseFeedException("Such feed is already in the DB", feedInfo);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/feeds/{id}")
    public Response update(@PathParam("id") Long id, RssFeed rssFeed) {
        RssFeed originRssFeed = dao.getById(id);

        if (originRssFeed == null) {
            System.err.println("Nothing to update: no such element by this ID.");
            throw new FeedNotFoundException
                    ("Nothing to update: no such element by this ID.");
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
            throw new FeedNotFoundException("RSS item with such ID is not found.");
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

        if (item == null)
            throw new ItemsNotFoundException("Item not found.");

        ItemInfo information = new ItemInfo();
        information.setInfo(item);
        return information;
    }


    @POST
    @Path("feeds/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postForm(@FormDataParam("DOCUMENT") InputStream document) {
        FeedParser parser = new FeedParser();
        List<RssFeed> feeds = parser.parseFeeds(document);
        if (feeds == null)
            throw new MultiPartQueryException("Unable to read sources from document.");

        feeds.stream().forEach(feed -> dao.add(feed));
        return Response.status(Response.Status.OK).build();
    }

}
