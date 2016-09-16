package org.tiwpr.szymie.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.SeasonCreationTaskDao;
import org.tiwpr.szymie.models.*;
import org.tiwpr.szymie.models.Error;
import org.tiwpr.szymie.resources.SaveResult;
import org.tiwpr.szymie.tasks.SeasonCreationAsyncTask;

import java.util.Optional;

@Component
public class SeasonCreationTaskUseCase {

    @Autowired
    private SeasonUseCase seasonUseCase;
    @Autowired
    private SeasonCreationTaskDao seasonCreationTaskDao;
    @Autowired
    private SeasonCreationAsyncTask seasonCreationAsyncTask;


    public SaveResult addSeasonCreationTask(Season season) {

        Optional<Error> errorOptional = validate(season);

        if(errorOptional.isPresent()) {
            return new SaveResult(errorOptional.get());
        }

        SeasonCreationTask seasonCreationTask = new SeasonCreationTask();
        seasonCreationTask.setStatus("in progress");

        int seasonCreationTaskId = seasonCreationTaskDao.save(seasonCreationTask);

        seasonCreationAsyncTask.createSeason(season, seasonCreationTaskId);

        return new SaveResult(seasonCreationTaskId);
    }

    private Optional<Error> validate(Season season) {

        if(!seasonUseCase.isLastSeasonCompleted()) {
            return Optional.of(new Error("Last season is not completed"));
        }

        if(!seasonCreationTaskDao.findByStatus("in progress").isEmpty()) {
            return Optional.of(new Error("There is one creation season task in progress"));
        }

        return Optional.empty();
    }


}
