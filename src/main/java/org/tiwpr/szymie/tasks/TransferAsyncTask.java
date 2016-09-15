package org.tiwpr.szymie.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tiwpr.szymie.daos.TransferDao;
import org.tiwpr.szymie.entities.ClubEntity;
import org.tiwpr.szymie.entities.PlayerEntity;
import org.tiwpr.szymie.entities.TransferEntity;
import org.tiwpr.szymie.models.Transfer;
import org.tiwpr.szymie.usecases.ClubPlayerUseCase;
import java.util.Optional;

@Component
public class TransferAsyncTask {

    @Autowired
    private TransferDao transferDao;
    @Autowired
    private ClubPlayerUseCase clubPlayerUseCase;

    //TODO - race condition
    @Async("asyncExecutor")
    @Transactional
    public void performTransfer(Transfer transfer) {

        delay(15000);

        Optional<TransferEntity> transferEntityOptional = transferDao.findById(transfer.getId());

        transferEntityOptional.ifPresent(transferEntity -> {
            PlayerEntity playerEntity = transferEntity.getPlayer();
            ClubEntity sourceClubEntity = transferEntity.getSourceClub();
            ClubEntity destinationClubEntity = transferEntity.getDestinationClub();

            clubPlayerUseCase.unbindPlayerWithClub(playerEntity, sourceClubEntity);
            clubPlayerUseCase.bindPlayerWithClub(playerEntity, destinationClubEntity);
        });

        transferEntityOptional.orElseThrow(() -> new RuntimeException("TransferAsyncTask::performTransfer : no transfer found"));

        transferDao.findById(transfer.getId()).map(transferEntity -> {
            transferEntity.setStatus("done");
            return transferEntity;
        }).ifPresent(transferDao::update);
    }

    private void delay(long milliseconds) {

        try {
            Thread.sleep(milliseconds);
            System.err.println("delay exit");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
