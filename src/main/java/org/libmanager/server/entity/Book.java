package org.libmanager.server.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("BOOK")
public class Book extends Item {

    @Column(length = 64)
    private String publisher = null;

    @Column(length = 13)
    private String isbn = null;

    public Book(
            int id, boolean status, int availableCopies, int totalCopies,
            String author, String title, LocalDate releaseDate, String genre,
            String publisher, String isbn
    ) {
        super(id, status, availableCopies, totalCopies, author, title, releaseDate, genre);
        this.publisher = publisher;
        this.isbn = isbn;
    }

    public Book() { }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
