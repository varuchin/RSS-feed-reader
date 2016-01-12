package com.mera.varuchin.filters;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Deprecated
public class RestAuthenticationFilter implements Filter {
    public static final String AUTHENTICATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);

            AuthenticationResource authenticationResource = new AuthenticationResource();

            boolean authenticationStatus = authenticationResource.authenticate(authCredentials);

            if (authenticationStatus) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                if (servletResponse instanceof HttpServletResponse) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}

