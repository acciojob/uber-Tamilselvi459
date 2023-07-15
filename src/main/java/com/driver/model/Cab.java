package com.driver.model;

import javax.persistence.*;

@Entity
public class Cab{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int kmperrate;

    private boolean available;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKmperrate() {
        return kmperrate;
    }

    public void setKmperrate(int kmperrate) {
        this.kmperrate = kmperrate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Cab(int id, int kmperrate, boolean available) {
        this.id = id;
        this.kmperrate = kmperrate;
        this.available = available;
    }
    @OneToOne
    @JoinColumn
    Driver driver;
}