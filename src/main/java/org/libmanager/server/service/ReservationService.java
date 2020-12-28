package org.libmanager.server.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.libmanager.server.entity.Book;
import org.libmanager.server.entity.DVD;
import org.libmanager.server.entity.Item;
import org.libmanager.server.entity.Reservation;
import org.libmanager.server.entity.User;
import org.libmanager.server.model.Response;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.repository.ReservationRepository;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.specification.ReservationSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Add a reservation
     * @param username  The user who wants to borrow an item
     * @param itemId    The id of the item the user wants to borrow
     * @return          A JSON response with true if the reservation was successfully created, the error and false otherwise
     */
    public Response<Boolean> add(String username, long itemId) {
        Optional<User> foundUser = userRepository.findById(username);
        Optional<Item> foundItem = itemRepository.findById(itemId);
        User user = foundUser.orElse(null);
        Item item = foundItem.orElse(null);
        if (user != null && item != null) {
            if (checkReservationLimits(user, item)) {
                if (item.getAvailableCopies() > 0) {
                    // Create the reservation
                    Reservation reservation = new Reservation();
                    reservation.setUser(user);
                    reservation.setItem(item);

                    // Decrement the number of available copies
                    item.setAvailableCopies(item.getAvailableCopies() - 1);

                    reservationRepository.save(reservation);
                    itemRepository.save(item);
                    return new Response<>(Response.Code.OK, true);
                }
                return new Response<>(Response.Code.NOT_AVAILABLE, false);
            }
            return new Response<>(Response.Code.MAX_RESERVATIONS_REACHED, false);
        }
        return new Response<>(Response.Code.NOT_FOUND, false);
    }

    public Response<Boolean> delete(long id) {
        Optional<Reservation> foundReservation = reservationRepository.findById(id);
        if (foundReservation.isPresent()) {
            Reservation reservation = foundReservation.get();
            Item item = reservation.getItem();

            // Increment the number of available copies
            item.setAvailableCopies(item.getAvailableCopies() + 1);

            reservationRepository.delete(reservation);
            itemRepository.save(item);
            return new Response<>(Response.Code.OK, true);
        }
        return new Response<>(Response.Code.NOT_FOUND, false);
    }

    /**
     * Get all reservations
     * @return  All reservations in the database
     */
    public Iterable<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    /**
     * Get a reservation
     * @param id    The id of the reservation to find
     * @return      The reservation if found, null otherwise
     */
    public Reservation get(long id) {
        Optional<Reservation> reservationToFind = reservationRepository.findById(id);
        return reservationToFind.orElse(null);
    }

    /**
     * Search reservations
     * @param id        The reservation id to find
     * @param username  The username to find
     * @param title     The title to find
     * @return          An iterable object with all found reservations
     */
    public Iterable<Reservation> search(long id, String username, String title) {
        User user = new User();
        // Any subclass of Item can be used
        Item item = new Book();
        user.setUsername('%' + username + '%');
        item.setTitle('%' + title + '%');
        Reservation filter = new Reservation();
        filter.setId(id);
        filter.setItem(item);
        filter.setUser(user);

        Specification<Reservation> spec = new ReservationSpecification(filter);

        return reservationRepository.findAll(spec);
    }

    /**
     * Check if a user can borrow an item
     * @param user  The user who wants to borrow an item
     * @param item  The item the user wants to borrow
     * @return      True if the user can borrow the item, false otherwise
     */
    private boolean checkReservationLimits(User user, Item item) {
        // Admin users can borrow as many items they want
        if (!user.isAdmin()) {
            // Membership duration in years
            long membershipDuration = ChronoUnit.YEARS.between(user.getRegistrationDate(), LocalDate.now());
            // If the user is older than 12, then we consider it as adult
            boolean isAdult = ChronoUnit.YEARS.between(user.getBirthday(), LocalDate.now()) >= 12;
            int nbBorrowed = 0;

            if (item instanceof Book) {
                // Get the number of books borrowed by the user
                for (Reservation r : user.getReservations()) {
                    if (r.getItem() instanceof Book)
                        nbBorrowed++;
                }
                return checkBookReservationLimits(membershipDuration, isAdult, nbBorrowed);
            } else if (item instanceof DVD) {
                // Get the number of DVDs borrowed by the user
                for (Reservation r : user.getReservations()) {
                    if (r.getItem() instanceof DVD)
                        nbBorrowed++;
                }
                return checkDVDReservationLimits(membershipDuration, isAdult, nbBorrowed);
            }
        }
        return false;
    }

    /**
     * Check if a user can borrow a book
     * @param membershipDuration    The number of years since the registration date of the user
     * @param isAdult               True if the user is older than 12, false otherwise
     * @param nbBorrowed            The number of books already borrowed by the user
     * @return                      True if the user can borrow a book, false otherwise
     */
    private boolean checkBookReservationLimits(long membershipDuration, boolean isAdult, int nbBorrowed) {
        if (isAdult) {
            // During his first year of membership, the adult user can borrow up to 4 books
            if (membershipDuration < 1)
                return nbBorrowed < 4;
            // During his second year of membership, the adult user can borrow up to 5 books
            else if (membershipDuration < 2)
                return nbBorrowed < 5;
            // After three years of membership, the adult user can borrow up to 7 books
            else
                return nbBorrowed < 7;
        }
        // Children can borrow up to 5 books
        else {
            return nbBorrowed < 5;
        }
    }

    /**
     * Check if a user can borrow a DVD
     * @param membershipDuration    The number of years since the registration date of the user
     * @param isAdult               True if the user is older than 12, false otherwise
     * @param nbBorrowed            The number of DVDs already borrowed by the user
     * @return                      True if the user can borrow a DVD, false otherwise
     */
    private boolean checkDVDReservationLimits(long membershipDuration, boolean isAdult, int nbBorrowed) {
        if (isAdult) {
            // During his first year of membership, the adult user can borrow up to 2 DVDs
            if (membershipDuration < 1)
                return nbBorrowed < 2;
            // During his second year of membership, the adult user can borrow up to 3 DVDs
            else if (membershipDuration < 2)
                return nbBorrowed < 3;
            // After three years of membership, the adult user can borrow up to 5 DVDs
            else
                return nbBorrowed < 5;
        }
        // Children can't borrow DVDs
        else {
            return false;
        }
    }

}
