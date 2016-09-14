package org.tiwpr.szymie.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.BaseEntity;
import org.tiwpr.szymie.entities.FixtureEntity;
import org.tiwpr.szymie.models.Fixture;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FixtureDao {

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

    public Optional<Fixture> findByClubsAndLeagueIdAndSeasonId(Fixture fixture, int leagueId, int seasonId) {

        TypedQuery<FixtureEntity> query = entityManager.createQuery(
                "from FixtureEntity f " +
                        "where f.homeClub.id = :homeClub and " +
                        "f.awayClub.id = :awayClub and " +
                        "f.league.id = :league and " +
                        "f.season.id = :season", FixtureEntity.class);

        query.setParameter("homeClub", fixture.getHomeClubId());
        query.setParameter("awayClub", fixture.getAwayClubId());
        query.setParameter("league", leagueId);
        query.setParameter("season", seasonId);

        List<FixtureEntity> list = query.getResultList();

        return list.stream().findFirst().map(FixtureEntity::toModel);
    }

    public List<Fixture> findByLeagueIdAndSeasonId(int leagueId, int seasonId) {

        TypedQuery<FixtureEntity> query = createFindByLeagueIdAndSeasonIdQuery(leagueId, seasonId);

        List<FixtureEntity> list = query.getResultList();

        return list.stream().map(FixtureEntity::toModel).collect(Collectors.toList());
    }

    private TypedQuery<FixtureEntity> createFindByLeagueIdAndSeasonIdQuery(int leagueId, int seasonId) {

        TypedQuery<FixtureEntity> query = entityManager.createQuery(
                "from FixtureEntity f where f.league.id = :league and f.season.id = :season", FixtureEntity.class);

        query.setParameter("league", leagueId);
        query.setParameter("season", seasonId);

        return query;
    }

    public List<Fixture> findByLeagueIdAndSeasonId(int leagueId, int seasonId, int offset, int limit) {

        TypedQuery<FixtureEntity> query = createFindByLeagueIdAndSeasonIdQuery(leagueId, seasonId);

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

    public void update(Fixture fixture) {

        Optional<FixtureEntity> fixtureEntityOptional = findById(fixture.getId());

        if(fixtureEntityOptional.isPresent()) {

            FixtureEntity fixtureEntity = fixtureEntityOptional.get();

            fixtureEntity.setMatchDay(fixture.getMatchDay());
            fixtureEntity.setDate(BaseEntity.dateFromString(fixture.getDate()));

            clubDao.findById(fixture.getHomeClubId()).ifPresent(fixtureEntity::setHomeClub);
            clubDao.findById(fixture.getAwayClubId()).ifPresent(fixtureEntity::setAwayClub);

            fixtureEntity.getResult().setGoalsHomeClub(fixture.getResult().getGoalsHomeClub());
            fixtureEntity.getResult().setGoalsAwayClub(fixture.getResult().getGoalsAwayClub());

            fixtureEntity.setLastModified(null);

            entityManager.persist(fixtureEntity);
        }
    }

    public void delete(int id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
