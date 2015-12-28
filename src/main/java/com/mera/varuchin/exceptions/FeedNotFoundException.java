package com.mera.varuchin.exceptions;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class FeedNotFoundException extends WebApplicationException{

    public FeedNotFoundException(){
        super(Response.Status.NOT_FOUND);
    }

    public FeedNotFoundException(String message){
        super(Response.status(Response.Status.NOT_FOUND)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
