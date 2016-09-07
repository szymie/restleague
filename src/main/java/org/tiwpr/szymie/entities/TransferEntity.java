package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.Transfer;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transfers")
public class TransferEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String status;
    private int value;
    private Date date;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private PlayerEntity player;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_club_id")
    private ClubEntity sourceClub;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_club_id")
    private ClubEntity destinationClub;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public ClubEntity getSourceClub() {
        return sourceClub;
    }

    public void setSourceClub(ClubEntity sourceClub) {
        this.sourceClub = sourceClub;
    }

    public ClubEntity getDestinationClub() {
        return destinationClub;
    }

    public void setDestinationClub(ClubEntity destinationClub) {
        this.destinationClub = destinationClub;
    }

    public Transfer toModel() {

        Transfer transfer = new Transfer();

        transfer.setId(id);
        transfer.setStatus(status);
        transfer.setValue(value);
        transfer.setDate(stringFromDate(date));
        transfer.setPlayerId(player.getId());
        transfer.setSourceClubId(sourceClub.getId());
        transfer.setDestinationClubId(destinationClub.getId());

        return transfer;
    }

    public static TransferEntity fromModel(Transfer transfer) {

        TransferEntity transferEntity = new TransferEntity();

        transferEntity.setId(transfer.getId());
        transferEntity.setStatus(transfer.getStatus());
        transferEntity.setValue(transfer.getValue());
        transferEntity.setDate(dateFromString(transfer.getDate()));

        return transferEntity;
    }
}
