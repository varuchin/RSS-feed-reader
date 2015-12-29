package com.mera.varuchin.exceptions;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MultiPartQueryException extends WebApplicationException {
    public MultiPartQueryException() {
        super(Response.Status.NOT_FOUND);
    }

    public MultiPartQueryException(String message) {
        super(Response.status(Response.Status.NOT_FOUND)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
