package org.tiwpr.szymie.daos;

import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.BaseEntity;
import org.tiwpr.szymie.entities.SeasonEntity;
import org.tiwpr.szymie.models.Player;
import org.tiwpr.szymie.models.Season;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SeasonDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<SeasonEntity> findById(int id) {
        SeasonEntity playerEntity = entityManager.find(SeasonEntity.class, id);
        return Optional.ofNullable(playerEntity);
    }

    public List<Season> findAll(int offset, int limit) {

        TypedQuery<SeasonEntity> query = entityManager.createQuery("from SeasonEntity", SeasonEntity.class);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<SeasonEntity> list = query.getResultList();

        return list.stream().map(SeasonEntity::toModel).collect(Collectors.toList());
    }

    public int save(Season season) {
        SeasonEntity seasonEntity = SeasonEntity.fromModel(season);
        entityManager.persist(seasonEntity);
        return seasonEntity.getId();
    }

    public void update(Season season) {

        Optional<SeasonEntity> seasonEntityOptional = findById(season.getId());

        if (seasonEntityOptional.isPresent()) {

            SeasonEntity seasonEntity = seasonEntityOptional.get();

            seasonEntity.setName(season.getName());
            seasonEntity.setStartDate(BaseEntity.dateFromString(season.getStartDate()));
            seasonEntity.setEndDate(BaseEntity.dateFromString(season.getEndDate()));
            seasonEntity.setStatus(season.getStatus());

            seasonEntity.setLastModified(null);

            entityManager.persist(seasonEntity);
        }
    }

    public void delete(int id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
