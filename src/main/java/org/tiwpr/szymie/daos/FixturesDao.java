package org.tiwpr.szymie.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.FixtureEntity;
import org.tiwpr.szymie.models.Fixture;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FixturesDao {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ClubDao clubDao;
    @Autowired
    private SeasonDao seasonDao;
    @Autowired
    private LeagueDao leagueDao;

    public Optional<FixtureEntity> findById(int id) {
        FixtureEntity fixtureEntity = entityManager.find(FixtureEntity.class, id);
        return Optional.ofNullable(fixtureEntity);
    }

    public List<Fixture> findAll(int offset, int limit) {

        TypedQuery<FixtureEntity> query = entityManager.createQuery("from FixtureEntity", FixtureEntity.class);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<FixtureEntity> list = query.getResultList();

        return list.stream().map(FixtureEntity::toModel).collect(Collectors.toList());
    }

    public int save(Fixture fixture, int seasonId, int leagueId) {

        FixtureEntity fixtureEntity = FixtureEntity.fromModel(fixture);

        clubDao.findById(fixture.getHomeClubId()).ifPresent(fixtureEntity::setHomeClub);
        clubDao.findById(fixture.getAwayClubId()).ifPresent(fixtureEntity::setAwayClub);

        seasonDao.findById(seasonId).ifPresent(fixtureEntity::setSeason);
        leagueDao.findById(leagueId).ifPresent(fixtureEntity::setLeague);

        entityManager.persist(fixtureEntity);

        return fixtureEntity.getId();
    }

    public void delete(int id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
