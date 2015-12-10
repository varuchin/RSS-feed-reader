package com.mera.varuchin.service;


import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.dao.RssItemDAOImpl;
import com.mera.varuchin.rss.RssItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;

@Path("/rss")
@Produces(MediaType.APPLICATION_XML)
public class RssService {

    private RssItemDAO dao;

    public RssService() {
        dao = new RssItemDAOImpl();
    }


    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response add(RssItem rssItem) {
        dao.add(rssItem);
        URI location = URI.create("/rss" + rssItem.getId());
        return Response.created(location).build();
    }

    /**
     * РАЗОБРАТЬСЯ КАК РАНДОМИТЬ ID В БАЗУ
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response update(@PathParam("id") Long id, RssItem rssItem) {
        RssItem originRssItem = new RssItem();

        boolean isNewRssItem;

        if (dao.getById(id) == null)
            isNewRssItem = true;
        else
            isNewRssItem = false;

        if (isNewRssItem)
            /////////////////////////////////////////////////////////////////////////
            originRssItem.setId(Long.valueOf(12));
            //////////////////////////////////////////////////////////////////////////////
        else
            originRssItem.setId(id);

        originRssItem.setName(rssItem.getName());
        originRssItem.setDescription(rssItem.getDescription());
        originRssItem.setLink(rssItem.getLink());
        originRssItem.setPubDate(rssItem.getPubDate());
        originRssItem.setTitle(rssItem.getTitle());

        dao.update(originRssItem);

        StringBuilder builder = new StringBuilder();
        String path = "/rss/";
        String rssItemID = rssItem.getId().toString();
        builder.append(path);
        builder.append(rssItemID);
        String loc = builder.toString();

        URI location = URI.create(loc);

        if (isNewRssItem)
            return Response.created(location).build();
        else
            return Response.noContent().location(location).build();
    }

    //ТЕСТИРОВАТЬ
    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Long id){
        RssItem originRssItem = new RssItem();
        originRssItem.setId(id);

        if(originRssItem.equals(null))
            return Response.status(Response.Status.NOT_FOUND).build();
        dao.remove(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/all")
    public Collection<RssItem> getAll() {
        System.err.println("All");
        return dao.getAllRss();
    }

}
