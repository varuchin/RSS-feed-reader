package com.mera.varuchin.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class ItemsNotFoundException extends WebApplicationException {


    public ItemsNotFoundException() {
        super(Response.Status.NOT_FOUND);
    }

    public ItemsNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND)
                .entity(message).type(MediaType.APPLICATION_JSON).build());
    }
}
