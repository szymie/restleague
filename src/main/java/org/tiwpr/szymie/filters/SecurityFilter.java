package org.tiwpr.szymie.filters;

import org.glassfish.jersey.internal.util.Base64;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;

@Provider
public class SecurityFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
    private static final String USERNAME_PASSWORD_SEPARATOR = ":";

    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        if(request.getMethod().equals(HttpMethod.POST)) {
            authenticate(request);
        }
    }

    private void authenticate(ContainerRequestContext request) {

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader != null) {

            String decodedAuthorizationHeader = extractCredentials(authorizationHeader);

            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthorizationHeader, USERNAME_PASSWORD_SEPARATOR);

            String username = getToken(stringTokenizer).orElse("");
            String password = getToken(stringTokenizer).orElse("");

            if(!checkCredentials(username, password)) {
                request.abortWith(unauthorizedResponse());
            }
        } else {
            request.abortWith(unauthorizedResponse());
        }
    }

    private String extractCredentials(String authorizationHeader) {
        authorizationHeader  = authorizationHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
        return Base64.decodeAsString(authorizationHeader);
    }

    private Optional<String> getToken(StringTokenizer stringTokenizer) {

        try {
            return Optional.of(stringTokenizer.nextToken());
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    private boolean checkCredentials(String username, String password) {
        return "admin".equals(username) && "admin".equals(password);
    }

    private Response unauthorizedResponse() {
        return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized resource cannot access this resource").build();
    }
}
