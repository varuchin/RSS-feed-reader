package com.mera.varuchin.exceptions;


import com.mera.varuchin.info.FeedInfo;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class DataBaseFeedException extends WebApplicationException {

    public DataBaseFeedException() {
        super(Response.Status.BAD_REQUEST);
    }

    public DataBaseFeedException(String message, FeedInfo feedInfo) {
        super(Response.status(Response.Status.BAD_REQUEST).header("Explanation: ", message)
                .entity(feedInfo).type(MediaType.APPLICATION_JSON).build());
    }
}
