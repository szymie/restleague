package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.Club;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "clubs")
public class ClubEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "date_of_founding")
    private Date dateOfFounding;
    @Column(name = "stadium")
    private String stadium;
    @Column(name = "last_modified")
    private Timestamp lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getDateOfFounding() {
        return dateOfFounding;
    }

    public void setDateOfFounding(Date dateOfFounding) {
        this.dateOfFounding = dateOfFounding;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public Club toModel() {

        Club club = new Club();
        club.setId(id);
        club.setFullName(fullName);
        club.setNickname(nickname);
        club.setDateOfFounding(stringFromDate(dateOfFounding));
        club.setStadium(stadium);

        return club;
    }

    public static ClubEntity fromModel(Club club) {

        ClubEntity clubEntity = new ClubEntity();

        clubEntity.setId(club.getId());
        clubEntity.setFullName(club.getFullName());
        clubEntity.setNickname(club.getNickname());
        clubEntity.setDateOfFounding(dateFromString(club.getDateOfFounding()));
        clubEntity.setStadium(club.getStadium());

        return clubEntity;
    }
}
