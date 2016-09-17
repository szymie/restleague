package org.tiwpr.szymie.resources;


import org.springframework.beans.factory.annotation.Autowired;
import org.tiwpr.szymie.daos.PoeKeyDao;
import org.tiwpr.szymie.models.Link;
import org.tiwpr.szymie.models.ModelWithLinks;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    protected <T> void fillModelWithLinks(ModelWithLinks<List<T>> modelWithLinks, List<T> entities, long numberOfAllEntities, UriInfo uriInfo, PaginationFilter paginationFilter) {

        PaginationFilter prev = calculatePrevOffsetAndLimit(paginationFilter);
        PaginationFilter next = calculateNextOffsetAndLimit(paginationFilter);

        List<Link> links = new ArrayList<>();

        links.add(new Link("self", uriInfo.getAbsolutePath().toASCIIString()));

        links.add(new Link("first", uriInfo.getAbsolutePathBuilder()
                .queryParam("offset", 0)
                .queryParam("limit", Math.min(numberOfAllEntities, paginationFilter.getLimit())).build().toASCIIString()));

        if(prev.getLimit() != 0) {
            links.add(new Link("prev", uriInfo.getAbsolutePathBuilder()
                    .queryParam("offset", prev.getOffset())
                    .queryParam("limit", prev.getLimit()).build().toASCIIString()));
        }

        if(numberOfAllEntities > paginationFilter.getOffset() + paginationFilter.getLimit()) {
            links.add(new Link("next", uriInfo.getAbsolutePathBuilder()
                    .queryParam("offset", next.getOffset())
                    .queryParam("limit", next.getLimit()).build().toASCIIString()));
        }

        links.add(new Link("last", uriInfo.getAbsolutePathBuilder()
                .queryParam("offset", Math.max(numberOfAllEntities - paginationFilter.getLimit(), 0))
                .queryParam("limit", Math.min(numberOfAllEntities, paginationFilter.getLimit())).build().toASCIIString()));

        modelWithLinks.setContent(entities);
        modelWithLinks.setLinks(links);
    }

    private PaginationFilter calculatePrevOffsetAndLimit(PaginationFilter paginationFilter) {

        int offset = paginationFilter.getOffset();
        int limit = paginationFilter.getLimit();

        int prevOffset = Math.max(offset - limit, 0);
        int prevLimit = Math.min(offset, limit);

        return new PaginationFilter(prevOffset, prevLimit);
    }

    private PaginationFilter calculateNextOffsetAndLimit(PaginationFilter paginationFilter) {

        int offset = paginationFilter.getOffset();
        int limit = paginationFilter.getLimit();

        int nextOffset = offset + limit;

        return new PaginationFilter(nextOffset, limit);
    }

    protected <T> List<T> subList(List<T> list, PaginationFilter paginationFilter) {
        return list.stream().skip(paginationFilter.getOffset()).limit(paginationFilter.getLimit()).collect(Collectors.toList());
    }
}
