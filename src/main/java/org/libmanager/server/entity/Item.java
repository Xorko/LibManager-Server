package org.libmanager.server.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type")
public abstract class Item implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private boolean status = true;

    @Column(nullable = false, length = 64)
    private String author;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private int totalCopies;

    @Column(nullable = false)
    private int availableCopies = totalCopies;

    @OneToMany(mappedBy = "item")
    private List<Reservation> reservations = new ArrayList<>();

    public Item(
            int id, boolean status, int availableCopies, int totalCopies,
            String author, String title, LocalDate releaseDate, String genre
    ) {
        assert totalCopies >= availableCopies;
        this.id = id;
        this.status = status;
        this.author = author;
        this.title = title;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public Item() { }

    public long getId() {
        return id;
    }

    public boolean getStatus() {
        return status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies <= totalCopies && availableCopies >= 0)
            this.availableCopies = availableCopies;
        status = this.availableCopies != 0;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

}