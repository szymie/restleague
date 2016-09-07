package org.tiwpr.szymie.resources;


import org.springframework.beans.factory.annotation.Autowired;
import org.tiwpr.szymie.daos.PoeKeyDao;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Optional;

public class BaseResource {

    @Autowired
    protected PoeKeyDao poeKeyDao;

    protected Response.ResponseBuilder notFoundResponseBuilder(Object entity) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(entity);
    }

    protected Response responseWithNewPoeKey(UriInfo uriInfo) {
        String newPoeKey = poeKeyDao.getNew();
        URI poeUri = uriInfo.getAbsolutePathBuilder().matrixParam("poe", newPoeKey).build();
        return Response.status(Response.Status.TEMPORARY_REDIRECT).location(poeUri).build();
    }

    protected Optional<Response.ResponseBuilder> evaluatePreconditions(Timestamp sentLastModified, Timestamp lastModified) {

        if(sentLastModified != null) {

            if(arePreconditionsMatched(sentLastModified, lastModified)) {
                return Optional.empty();
            } else {
                return Optional.of(Response.status(Response.Status.PRECONDITION_FAILED));
            }
        } else {
            return Optional.of(Response.status(Response.Status.FORBIDDEN));
        }
    }

    private boolean arePreconditionsMatched(Timestamp sentLastModified, Timestamp lastModified) {
        return sentLastModified.compareTo(lastModified) == 0;
    }
}
