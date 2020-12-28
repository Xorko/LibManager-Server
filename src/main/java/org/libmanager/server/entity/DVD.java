package org.libmanager.server.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("DVD")
public class DVD extends Item {

    @Column(length = 16)
    private String duration = null;

    public DVD(
            int id, boolean status, int availableCopies, int totalCopies,
            String author, String title, LocalDate releaseDate, String genre,
            String duration
            ) {
        super(id, status, availableCopies, totalCopies, author, title, releaseDate, genre);
        this.duration = duration;
    }

    public DVD() { }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}
