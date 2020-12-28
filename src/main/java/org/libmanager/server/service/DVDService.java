package org.libmanager.server.service;

import java.util.Optional;

import org.libmanager.server.entity.DVD;
import org.libmanager.server.model.Response;
import org.libmanager.server.repository.DVDRepository;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.specification.DVDSpecification;
import org.libmanager.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DVDService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DVDRepository dvdRepository;

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
    public Response<Boolean> add(
            String title,
            String director,
            String duration,
            String genre,
            String releaseDate,
            int totalCopies
    ) {
        Long sumTotalCopies = itemRepository.sumTotalCopies();
        // If the item table is empty, sumTotalCopies will be null
        if (sumTotalCopies == null)
            sumTotalCopies = 0L;
        if (sumTotalCopies + totalCopies <= 100_000) {
            DVD dvd = new DVD();
            dvd.setTitle(title);
            dvd.setAuthor(director);
            dvd.setReleaseDate(DateUtil.parseDB(releaseDate));
            dvd.setDuration(duration);
            dvd.setGenre(genre.toUpperCase());
            dvd.setTotalCopies(totalCopies);
            dvd.setAvailableCopies(totalCopies);
            dvdRepository.save(dvd);
            return new Response<>(Response.Code.OK, true);
        }
        return new Response<>(Response.Code.MAX_ITEMS_REACHED, false);
    }

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
    public Response<Boolean> edit(
            long id,
            String title,
            String director,
            String duration,
            String genre,
            String releaseDate,
            int totalCopies
    ) {
        Optional<DVD> foundDVD = dvdRepository.findById(id);
        if (foundDVD.isPresent()) {
            DVD dvd = foundDVD.get();
            // New sum of all total_copies rows value should not be higher than 100,000
            if (itemRepository.sumTotalCopies() - dvd.getTotalCopies() + totalCopies <= 100_000) {
                // New total_copies value should not be lower than the number of available copies
                if (dvd.getTotalCopies() - dvd.getAvailableCopies() <= totalCopies) {
                    dvd.setTitle(title);
                    dvd.setAuthor(director);
                    dvd.setDuration(duration);
                    dvd.setGenre(genre.toUpperCase());
                    dvd.setReleaseDate(DateUtil.parseDB(releaseDate));
                    dvd.setTotalCopies(totalCopies);
                    dvd.setAvailableCopies(dvd.getAvailableCopies() + (totalCopies - dvd.getTotalCopies()));
                    dvdRepository.save(dvd);
                    return new Response<>(Response.Code.OK, true);
                }
                return new Response<>(Response.Code.INVALID_TOTAL_COPIES, false);
            }
            return new Response<>(Response.Code.MAX_ITEMS_REACHED, false);
        }
        return new Response<>(Response.Code.NOT_FOUND, false);
    }

    /**
     * Get all DVDs
     * @return  All DVDs in an iterable object
     */
    public Iterable<DVD> getAll() {
        return dvdRepository.findAll();
    }

    /**
     * Get a DVD by its id
     * @param id    The id of the DVD to get
     * @return      The DVD
     */
    public DVD get(long id) {
        Optional<DVD> foundDVD = dvdRepository.findById(id);
        return foundDVD.orElse(null);
    }

    /**
     * Search DVDs
     * @param title         The title to find
     * @param author        The author to find
     * @param genre         The genre to find
     * @param releaseDate   The release date to find
     * @param status        The status to find (should be null to find any status)
     * @return              An iterable object with all found DVDs
     */
    public Iterable<DVD> search(
            String title,
            String author,
            String genre,
            String releaseDate,
            String status
    ) {
        DVD filter = new DVD();
        filter.setTitle('%' + title + '%');
        filter.setAuthor('%' + author + '%');
        filter.setGenre(genre);
        filter.setReleaseDate(DateUtil.parseDB(releaseDate));

        Specification<DVD> spec = new DVDSpecification(filter, status);

        return dvdRepository.findAll(spec);
    }

}
