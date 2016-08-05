package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.Player;
import org.tiwpr.szymie.models.Position;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "positions")
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;

    public PositionEntity() {
    }

    public PositionEntity(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position toPosition() {
        return new Position(name);
    }
}
