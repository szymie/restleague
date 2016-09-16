package org.tiwpr.szymie.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.SeasonCreationTaskEntity;
import org.tiwpr.szymie.entities.SeasonEntity;
import org.tiwpr.szymie.models.SeasonCreationTask;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SeasonCreationTaskDao {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private SeasonDao seasonDao;

    public Optional<SeasonCreationTaskEntity> findById(int id) {
        SeasonCreationTaskEntity seasonCreationTaskEntity = entityManager.find(SeasonCreationTaskEntity.class, id);
        return Optional.ofNullable(seasonCreationTaskEntity);
    }

    public List<SeasonCreationTask> findByStatus(String status) {

        TypedQuery<SeasonCreationTaskEntity> query = entityManager.createQuery(
                "from SeasonCreationTaskEntity sct where sct.status = :status", SeasonCreationTaskEntity.class);

        query.setParameter("status", status);

        List<SeasonCreationTaskEntity> list = query.getResultList();

        return list.stream().map(SeasonCreationTaskEntity::toModel).collect(Collectors.toList());
    }

    public int save(SeasonCreationTask seasonCreationTask) {

        SeasonCreationTaskEntity seasonCreationTaskEntity = SeasonCreationTaskEntity.fromModel(seasonCreationTask);

        fillPositionForSeasonCreationTask(seasonCreationTask, seasonCreationTaskEntity);

        entityManager.persist(seasonCreationTaskEntity);

        return seasonCreationTaskEntity.getId();
    }

    private void fillPositionForSeasonCreationTask(SeasonCreationTask seasonCreationTask, SeasonCreationTaskEntity seasonCreationTaskEntity) {
        Optional<Integer> seasonIdOptional = Optional.ofNullable(seasonCreationTask.getSeasonId());
        seasonIdOptional.ifPresent(seasonId ->seasonDao.findById(seasonId).ifPresent(seasonCreationTaskEntity::setSeason));
    }

    public void updateStatusToSuccess(int seasonCreationTaskId) {

        Optional<SeasonCreationTaskEntity> seasonCreationTaskEntityOptional = findById(seasonCreationTaskId);

        seasonCreationTaskEntityOptional.ifPresent(seasonCreationTaskEntity -> {
            seasonCreationTaskEntity.setStatus("success");
            entityManager.persist(seasonCreationTaskEntity);
        });
    }

    public void updateSeasonId(int seasonCreationTaskId, int seasonId) {

        Optional<SeasonCreationTaskEntity> seasonCreationTaskEntityOptional = findById(seasonCreationTaskId);

        Optional<SeasonEntity> seasonEntityOptional = seasonDao.findById(seasonId);

        seasonCreationTaskEntityOptional.ifPresent(seasonCreationTaskEntity ->
            seasonEntityOptional.ifPresent(seasonEntity ->  {
                seasonCreationTaskEntity.setSeason(seasonEntity);
                entityManager.persist(seasonCreationTaskEntity);
            })
        );
    }
}
