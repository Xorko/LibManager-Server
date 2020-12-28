package org.libmanager.server.service;

import org.libmanager.server.entity.DVD;
import org.libmanager.server.response.Response;

public interface DVDService {

    /**
     * Add a DVD
     * @param title         The title of the DVD
     * @param director      The author of the DVD
     * @param duration      The duration of the DVD
     * @param genre         The genre of the DVD
     * @param releaseDate   The release date of the DVD
     * @param totalCopies   The number of total copies of the DVD
     * @return              A response with OK code and true if the add was successful, the error and false otherwise
     */
    Response<Boolean> add(
            String title,
            String director,
            String duration,
            String genre,
            String releaseDate,
            int totalCopies
    );

    /**
     * Edit a DVD
     * @param id            The id of the DVD to edit
     * @param title         The new title of the DVD
     * @param director      The new director of the DVD
     * @param duration      The new duration of the DVD
     * @param genre         The new genre of the DVD
     * @param releaseDate   The new release date of the DVD
     * @param totalCopies   The new number of total copies of the DVD
     * @return              A response with OK code and true if the edit was successful, the error and false otherwise
     */
    Response<Boolean> edit(
            long id,
            String title,
            String director,
            String duration,
            String genre,
            String releaseDate,
            int totalCopies
    );

    /**
     * Get all DVDs
     * @return  All DVDs in an iterable object
     */
    Iterable<DVD> getAll();

    /**
     * Get a DVD by its id
     * @param id    The id of the DVD to get
     * @return      The DVD
     */
    DVD get(long id);

    /**
     * Search DVDs
     * @param title         The title to find
     * @param author        The author to find
     * @param genre         The genre to find
     * @param releaseDate   The release date to find
     * @param status        The status to find (should be null to find any status, "0" for unavailable and "1" for available)
     * @return              An iterable object with all found DVDs
     */
    Iterable<DVD> search(
            String title,
            String author,
            String genre,
            String releaseDate,
            String status
    );

}
