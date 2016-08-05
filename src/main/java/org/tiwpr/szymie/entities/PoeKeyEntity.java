package org.tiwpr.szymie.entities;

import javax.persistence.*;

@Entity
@Table(name = "poe_keys")
public class PoeKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String value;

    public PoeKeyEntity() {
    }

    public PoeKeyEntity(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
