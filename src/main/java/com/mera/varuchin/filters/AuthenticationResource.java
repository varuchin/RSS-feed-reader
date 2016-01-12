package com.mera.varuchin.filters;


import java.util.Base64;
import java.util.StringTokenizer;


public class AuthenticationResource {

    public boolean authenticate(String authCredentails) {

        if (authCredentails == null)
            return false;

        final String encodedUserPassword = authCredentails.replaceFirst("Basic"
                + " ", "");
        String userNameAndPassword = null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
            userNameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final StringTokenizer stringTokenizer =
                new StringTokenizer(userNameAndPassword, ":");
        final String userName = stringTokenizer.nextToken();
        final String userPassword = stringTokenizer.nextToken();

        boolean authenticationStatus = "user".equals(userName)
                && "user".equals(userPassword);

        return authenticationStatus;
    }
}
