package com.mera.varuchin.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AuthorizationException extends WebApplicationException {

    public AuthorizationException(){}

    public AuthorizationException(String message){
        super(Response.status(Response.Status.UNAUTHORIZED)
                //.header("Explanation: ", message)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
