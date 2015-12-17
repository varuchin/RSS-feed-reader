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

@Path("/rss/items")
@Produces(MediaType.APPLICATION_XML)
public class RssService {

    private RssItemDAO dao;

    public RssService() {
        dao = new RssItemDAOImpl();
    }

    @DELETE
    public Response delete(@PathParam("url") URL url) {
        dao.remove(url);
        return Response.ok().build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<RssItem> getAll() {
        System.err.println("All");
        return dao.getAllItems();
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
