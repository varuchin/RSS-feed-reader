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
import java.util.List;

@Path("/rss/items")
@Produces(MediaType.APPLICATION_JSON)
public class ItemService {

    private RssItemDAO dao;

    public ItemService() {
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
    public List<RssItem> getAll() {
        System.err.println("All");
        return dao.getAllItems();
    }

    @GET
    @Path("/sources/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllSources() {
        JSONObject json = new JSONObject(dao.getAllSourcesRss());
        return json.toString();
    }

    @GET
    @Path("/words")
    public String getTopWords(@QueryParam("item_id") Long item_id){
        JSONObject jsonObject = new JSONObject(dao.getTopWords(item_id));
        return jsonObject.toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(RssItem rssItem) {

        if (dao.getByLink(rssItem.getLink()) == null) {
            System.out.println(rssItem.getPubDate());
            dao.add(rssItem);
            URI location = URI.create("/rss" + rssItem.getId());
            return Response.created(location).build();

        } else
            return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
