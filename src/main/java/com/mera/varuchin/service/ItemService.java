package com.mera.varuchin.service;


import com.mera.varuchin.dao.RssItemDAO;
import com.mera.varuchin.dao.RssItemDAOImpl;
import com.mera.varuchin.rss.RssItem;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/rss/items")
@Produces(MediaType.APPLICATION_JSON)
public class ItemService {

    private RssItemDAO dao;

    public ItemService() {
        dao = new RssItemDAOImpl();
    }

//переменовать и добавить пагинацию
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RssItem> getAll() {
        System.err.println("All");
        return dao.getAllItems();
    }


    @GET
    @Path("/{id}/words")
    public String getTopWords(@PathParam("item_id") Long item_id){
        JSONObject jsonObject = new JSONObject(dao.getTopWords(item_id));
        return jsonObject.toString();
    }
}
