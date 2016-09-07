package org.tiwpr.szymie.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.ClubDao;
import org.tiwpr.szymie.daos.PlayerDao;
import org.tiwpr.szymie.daos.TransferDao;
import org.tiwpr.szymie.entities.ClubEntity;
import org.tiwpr.szymie.entities.PlayerEntity;
import org.tiwpr.szymie.entities.TransferEntity;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.models.Transfer;
import org.tiwpr.szymie.usecases.ClubPlayerUseCase;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Path("transfers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransfersResource extends BaseResource {

    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private ClubDao clubDao;
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private ClubPlayerUseCase clubPlayerUseCase;

    @POST
    @Transactional
    public Response postTransfer(@Context UriInfo uriInfo, @Valid Transfer transfer) {

        if(transfer.getSourceClubId() != transfer.getDestinationClubId()) {

            if(clubPlayerUseCase.isPlayerBoundWithClub(transfer.getPlayerId(), transfer.getSourceClubId())) {

                if(clubDao.findById(transfer.getDestinationClubId()).isPresent()) {

                    if(!clubPlayerUseCase.hasPlayerInProgressTransfer(transfer.getPlayerId(), transfer.getSourceClubId())) {

                        transfer.setStatus("in progress");
                        int transferId = transferDao.save(transfer);
                        transfer.setId(transferId);

                        executor.execute(() -> performTransfer(transfer));

                        URI locationUri = uriInfo.getBaseUriBuilder().path(TransfersResource.class).path(Integer.toString(transferId)).build();
                        return Response.accepted().location(locationUri).build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN).entity(new Error("Requested player already has a transfer in progress")).build();
                    }
                } else {
                    return Response.status(Response.Status.FORBIDDEN).entity(new Error("Destination club does not exist")).build();
                }
            } else {
                return Response.status(Response.Status.FORBIDDEN).entity(new Error("Requested player is not bound to requested source club")).build();
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(new Error("sourceClubId and destinationClubId cannot be the same")).build();
        }
    }

    @Transactional
    private void performTransfer(Transfer transfer) {

        Optional<TransferEntity> transferEntityOptional = transferDao.findById(transfer.getId());

        transferEntityOptional.ifPresent(transferEntity -> {
            PlayerEntity playerEntity = transferEntity.getPlayer();
            ClubEntity sourceClubEntity = transferEntity.getSourceClub();
            ClubEntity destinationClubEntity = transferEntity.getDestinationClub();

            clubPlayerUseCase.unbindPlayerWithClub(playerEntity, sourceClubEntity);
            clubPlayerUseCase.bindPlayerWithClub(playerEntity, destinationClubEntity);
        });

        transferEntityOptional.orElseThrow(RuntimeException::new);

        transferDao.findById(transfer.getId()).map(transferEntity -> {
            transferEntity.setStatus("done");
            return transferEntity;
        }).ifPresent(transferDao::update);
    }

    @GET
    @Path("test2")
    public Response asyncGet2(@Context UriInfo uriInfo) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ASYNC");
        });

        URI uri = uriInfo.getAbsolutePathBuilder().path("123").build();

        return Response.status(Response.Status.ACCEPTED).location(uri).build();
    }
}
