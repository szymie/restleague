package org.tiwpr.szymie.daos;

import org.springframework.stereotype.Repository;
import org.tiwpr.szymie.entities.ClubEntity;
import org.tiwpr.szymie.entities.ClubLeagueSeasonEntryEntity;
import org.tiwpr.szymie.entities.LeagueEntity;
import org.tiwpr.szymie.models.Club;
import org.tiwpr.szymie.models.League;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ClubLeagueSeasonEntryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Club> findBySeasonIdAndLeagueId(int seasonId, int leagueId, int offset, int limit) {

        TypedQuery<ClubEntity> query = createFindBySeasonIdAndLeagueIdQuery(seasonId, leagueId);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<ClubEntity> list = query.getResultList();

        return list.stream().map(ClubEntity::toModel).collect(Collectors.toList());
    }

    private TypedQuery<ClubEntity> createFindBySeasonIdAndLeagueIdQuery(int seasonId, int leagueId) {

        TypedQuery<ClubEntity> query = entityManager.createQuery(
                "select cls.club from ClubLeagueSeasonEntryEntity cls where cls.season.id = :season and cls.league.id = :league",
                ClubEntity.class);

        query.setParameter("season", seasonId);
        query.setParameter("league", leagueId);

        return query;
    }

    public List<Club> findBySeasonIdAndLeagueId(int seasonId, int leagueId) {

        TypedQuery<ClubEntity> query = createFindBySeasonIdAndLeagueIdQuery(seasonId, leagueId);

        List<ClubEntity> list = query.getResultList();

        return list.stream().map(ClubEntity::toModel).collect(Collectors.toList());
    }

    public List<League> findByClubIdAndSeasonId(int clubId, int seasonId) {

        TypedQuery<LeagueEntity> query = entityManager.createQuery(
                "select cls.league from ClubLeagueSeasonEntryEntity cls where cls.season.id = :season and cls.club.id = :club",
                LeagueEntity.class);

        query.setParameter("club", clubId);
        query.setParameter("season", seasonId);

        List<LeagueEntity> list = query.getResultList();

        return list.stream().map(LeagueEntity::toModel).collect(Collectors.toList());
    }

    public void save(ClubLeagueSeasonEntryEntity entryEntity) {
        entityManager.persist(entryEntity);
    }

    public void deleteByClubIdAndLeagueIdAndSeasonId(int clubId, int leagueId, int seasonId) {

        TypedQuery<ClubLeagueSeasonEntryEntity> query = entityManager.createQuery(
                "from ClubLeagueSeasonEntryEntity cls where cls.season.id = :season and cls.league.id = :league and cls.club.id = :club",
                ClubLeagueSeasonEntryEntity.class);

        query.setParameter("season", seasonId);
        query.setParameter("league", leagueId);
        query.setParameter("club", clubId);

        query.getResultList().stream().forEach(entityManager::remove);
    }
}
