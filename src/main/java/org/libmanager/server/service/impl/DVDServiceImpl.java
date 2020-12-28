package org.libmanager.server.service.impl;

import java.util.Optional;

import org.libmanager.server.entity.DVD;
import org.libmanager.server.model.Response;
import org.libmanager.server.repository.DVDRepository;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.service.DVDService;
import org.libmanager.server.specification.DVDSpecification;
import org.libmanager.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DVDServiceImpl implements DVDService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DVDRepository dvdRepository;

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public Iterable<DVD> getAll() {
        return dvdRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public DVD get(long id) {
        Optional<DVD> foundDVD = dvdRepository.findById(id);
        return foundDVD.orElse(null);
    }

    /**
     * {@inheritDoc}
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
