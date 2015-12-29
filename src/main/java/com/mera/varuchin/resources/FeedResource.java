package com.mera.varuchin.resources;


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mera.varuchin.dao.RssFeedDAO;
import com.mera.varuchin.dao.RssFeedDAOImpl;
import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.exceptions.DataBaseFeedException;
import com.mera.varuchin.exceptions.FeedNotFoundException;
import com.mera.varuchin.filters.RestAuthenticationFilter;
import com.mera.varuchin.info.FeedInfo;
import com.mera.varuchin.info.ItemInfo;
import com.mera.varuchin.modules.FeedModule;
import com.mera.varuchin.modules.ItemModule;
import com.mera.varuchin.parsers.RssParser;
import com.mera.varuchin.rss.RssFeed;
import com.mera.varuchin.rss.RssItem;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Path("/rss")
@Produces(MediaType.APPLICATION_JSON)
public class FeedResource {

    @Inject
    private final RssFeedDAO dao = getFeedDAO();

    @Inject
    private final RssItemDAO itemDAO = getItemDAO();

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
        RestAuthenticationFilter restAutentificationFilter =
                new RestAuthenticationFilter();
        List<RssFeed> feeds = dao.getFeeds(page, papeSize, name);
        System.out.println(feeds.size());
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
        RssFeed originRssFeed = new RssFeedDAOImpl().getById(id);

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
        ItemInfo information = new ItemInfo();
        information.setInfo(item);
        return information;
    }


    @POST
    @Path("feeds/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void postForm(@FormDataParam("DOCUMENT") InputStream document) {
        RssParser rssParser = new RssParser();
        List<RssFeed> feeds = rssParser.parseFeeds(document);
        System.out.println(feeds);

        feeds.stream().forEach(feed-> dao.add(feed));
        System.err.println("123");
    }

    private RssFeedDAO getFeedDAO() {
        Injector injector = Guice.createInjector(new FeedModule());
        RssFeedDAO dao = injector.getInstance(RssFeedDAO.class);
        return dao;
    }

    private RssItemDAO getItemDAO() {
        Injector injector = Guice.createInjector(new ItemModule());
        RssItemDAO itemDAO = injector.getInstance(RssItemDAO.class);
        return itemDAO;
    }
}