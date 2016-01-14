package com.mera.varuchin.filters;


import com.mera.varuchin.exceptions.AuthorizationException;
import org.glassfish.jersey.internal.util.Base64;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.StringTokenizer;

public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        if (authorization == null || authorization.isEmpty())
            throw new AuthorizationException("ACCESS DENIED.");

        final String encodedUserPassword = authorization.get(0)
                .replaceFirst(AUTHENTICATION_SCHEME + " ", "");
        String usernameAndPassword =
                new String(Base64.decode(encodedUserPassword.getBytes()));

        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        if (!isUserAllowed(username, password)) {
            throw new AuthorizationException("Bad credentials.");
        }
    }

    private boolean isUserAllowed(String username, String password) {
        boolean isAllowed = false;
        if (username.equals("admin") && password.equals("admin")) {
            isAllowed = true;
        }

        return isAllowed;
    }
}