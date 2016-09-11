package org.tiwpr.szymie.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiwpr.szymie.daos.SeasonDao;

@Component
public class SeasonUseCase {

    @Autowired
    private SeasonDao seasonDao;

    public boolean isThereAnySeason() {
        return !seasonDao.findAll(0, 1).isEmpty();
    }

    public boolean isSeasonCompleted(int seasonId) {

        return seasonDao.findById(seasonId)
                .filter(seasonEntity -> seasonEntity.getStatus().equals("completed"))
                .isPresent();
    }
}
