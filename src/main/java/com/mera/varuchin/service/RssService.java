package com.mera.varuchin.service;


import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.dao.RssItemDAOImpl;
import com.mera.varuchin.rss.RssItem;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URL;
import java.util.Collection;



@Path("/rss")
@Produces(MediaType.APPLICATION_XML)
public class RssService {

    private RssItemDAO dao;

    public RssService() {
        dao = new RssItemDAOImpl();
    }


    //почему-то не парсится pubDate
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(RssItem rssItem) {
        System.out.println(rssItem);
        if (dao.getByLink(rssItem.getLink()) == null) {
            System.out.println(rssItem.getPubDate());
            dao.add(rssItem);
            URI location = URI.create("/rss" + rssItem.getId());
            return Response.created(location).build();

        } else
            return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, RssItem rssItem) {
        RssItem originRssItem = new RssItem();

        if (dao.getById(id) == null) {
            System.err.println("Nothing to update: no such element by this ID.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            System.err.println("RSS Item was found.");
            originRssItem.setName(rssItem.getName());
            originRssItem.setDescription(rssItem.getDescription());
            originRssItem.setLink(rssItem.getLink());
            originRssItem.setPubDate(rssItem.getPubDate());
            originRssItem.setTitle(rssItem.getTitle());

            dao.update(originRssItem);
            System.err.println("Updated.");
            return Response.ok().build();
        }
    }

    //
    //    работает, не трогать
    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Long id) {
        RssItem originRssItem = dao.getById(id);
        if (originRssItem == null) {
            System.err.println("RSS item with such ID is not found.");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        dao.remove(id);
        return Response.ok().build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RssItem> getAll() {
        System.err.println("All");
        return dao.getAllRss();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RssItem> getByName(@QueryParam("name") String name) {
        return dao.getRssSortedByName(name);
    }

    @GET
    @Path("/sources")
    @Produces(MediaType.APPLICATION_JSON)
    public RssItem getBySource(@QueryParam("title") String title,
                               @QueryParam("link") URL link) {
        return dao.getBySource(title, link);
    }

    @GET
    @Path("/sources/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllSources() {
        JSONObject json = new JSONObject(dao.getAllSourcesRss());
        return json.toString();
    }

//pagination в оракле погуглить

    //передавать старт и лимит в ресурсе
//   не работает
//    @GET
//    @Path("/list")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Pageable<RssItem> getPaginatedList(){
//        return dao.getPaginatedList();
//    }

}
