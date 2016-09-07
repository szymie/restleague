package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.Player;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "players")
public class PlayerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    private int height;
    private String foot;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private PositionEntity position;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private CountryEntity country;
    @Column(name = "last_modified")
    private Timestamp lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFoot() {
        return foot;
    }

    public void setFoot(String foot) {
        this.foot = foot;
    }

    public PositionEntity getPosition() {
        return position;
    }

    public void setPosition(PositionEntity position) {
        this.position = position;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public Player toModel() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        Player player = new Player();
        player.setId(id);
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setDateOfBirth(dateFormat.format(dateOfBirth));
        player.setHeight(height);
        player.setFoot(foot);
        player.setPosition(position.toPosition());
        player.setCountry(country.toCountry());

        return player;
    }

    public static PlayerEntity fromModel(Player player) {

        PlayerEntity playerEntity = new PlayerEntity();

        playerEntity.setId(player.getId());
        playerEntity.setFirstName(player.getFirstName());
        playerEntity.setLastName(player.getLastName());
        playerEntity.setDateOfBirth(dateFromString(player.getDateOfBirth()));
        playerEntity.setHeight(player.getHeight());
        playerEntity.setFoot(player.getFoot());

        return playerEntity;
    }
}
