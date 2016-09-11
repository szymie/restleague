package org.tiwpr.szymie.daos;

import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.LeagueEntity;
import org.tiwpr.szymie.models.League;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class LeagueDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<LeagueEntity> findById(int id) {
        LeagueEntity leagueEntity = entityManager.find(LeagueEntity.class, id);
        return Optional.ofNullable(leagueEntity);
    }

    public List<League> findAll() {

        TypedQuery<LeagueEntity> query = createFindAllQuery();

        List<LeagueEntity> list = query.getResultList();

        return list.stream().map(LeagueEntity::toModel).collect(Collectors.toList());
    }

    private TypedQuery<LeagueEntity> createFindAllQuery() {
        return entityManager.createQuery("from LeagueEntity", LeagueEntity.class);
    }

    public void save(List<League> leagues) {
        leagues.stream().map(LeagueEntity::fromModel).forEach(entityManager::persist);
    }

    public void deleteAll() {
        TypedQuery<LeagueEntity> query = createFindAllQuery();
        query.getResultList().forEach(entityManager::remove);
    }
}
