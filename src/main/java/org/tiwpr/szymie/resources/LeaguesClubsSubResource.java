package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.ClubLeagueSeasonEntryDao;
import org.tiwpr.szymie.models.Club;
import org.tiwpr.szymie.models.ClubId;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.ModelWithLinks;
import org.tiwpr.szymie.usecases.ClubLeagueSeasonUseCase;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LeaguesClubsSubResource extends BaseResource {

    @Autowired
    private ClubLeagueSeasonUseCase clubLeagueSeasonUseCase;
    @Autowired
    private ClubLeagueSeasonEntryDao clubLeagueSeasonEntryDao;

    @GET
    @Transactional
    public ModelWithLinks<List<Club>> getLeaguesClubs(
            @Context UriInfo uriInfo,
            @PathParam("seasonId") int seasonId,
            @PathParam("leagueId") int leagueId,
            @BeanParam PaginationFilter paginationFilter) {

        List<Club> clubs = clubLeagueSeasonEntryDao.findBySeasonIdAndLeagueId(seasonId, leagueId);
        List<Club> subClubs = clubs.subList(paginationFilter.getOffset(), paginationFilter.getLimit());

        ModelWithLinks<List<Club>> modelWithLinks = new ModelWithLinks<>();
        fillModelWithLinks(modelWithLinks, subClubs, clubs.size(), uriInfo, paginationFilter);

        return modelWithLinks;
    }

    @POST
    @Transactional
    public Response postLeaguesClubs(@PathParam("seasonId") int seasonId, @PathParam("leagueId") int leagueId, @Valid ClubId clubId) {

        Optional<Error> errorOptional = clubLeagueSeasonUseCase.bindClubWithLeagueAtSeason(clubId.getClubId(), leagueId, seasonId);

        if(errorOptional.isPresent()) {
            Error error = errorOptional.get();
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        } else {
            return Response.ok().build();
        }
    }

    @GET
    @Path("/{clubId}")
    @Transactional
    public Response getLeaguesClub(
            @PathParam("seasonId") int seasonId,
            @PathParam("leagueId") int leagueId,
            @PathParam("clubId") int clubId,
            @Context UriInfo uriInfo) {

        if(clubLeagueSeasonUseCase.isClubBoundWithLeagueAtSeason(clubId, leagueId, seasonId)) {
            URI clubUri = uriInfo.getBaseUriBuilder().path(ClubsResource.class).path(Integer.toString(clubId)).build();
            return Response.status(Response.Status.TEMPORARY_REDIRECT).location(clubUri).build();
        } else {
            return notFoundResponseBuilder(new Error("Requested club is not bound to requested league at requested season")).build();
        }
    }

    @DELETE
    @Path("/{clubId}")
    @Transactional
    public Response deleteLeaguesClub(
            @PathParam("seasonId") int seasonId,
            @PathParam("leagueId") int leagueId,
            @PathParam("clubId") int clubId) {

        Optional<Error> errorOptional = clubLeagueSeasonUseCase.unbindClubWithLeagueAtSeason(clubId, leagueId, seasonId);

        if(errorOptional.isPresent()) {
            Error error = errorOptional.get();
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        } else {
            return Response.noContent().build();
        }
    }
}
