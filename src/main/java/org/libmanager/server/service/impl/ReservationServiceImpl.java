package org.libmanager.server.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.libmanager.server.entity.Book;
import org.libmanager.server.entity.Item;
import org.libmanager.server.entity.Reservation;
import org.libmanager.server.entity.User;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.repository.ReservationRepository;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.ReservationService;
import org.libmanager.server.specification.ReservationSpecification;
import org.libmanager.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
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
     * {@inheritDoc}
     */
    public Iterable<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Reservation get(long id) {
        Optional<Reservation> reservationToFind = reservationRepository.findById(id);
        return reservationToFind.orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    public Response<Iterable<Reservation>> getByUser(String username) {
        Optional<User> foundUser = userRepository.findById(username);
        return foundUser.map(user -> new Response<>(Response.Code.OK, reservationRepository.findReservationsByUser(user)))
                        .orElseGet(() -> new Response<>(Response.Code.NOT_FOUND, null));
    }

    /**
     * {@inheritDoc}
     */
    public Iterable<Reservation> search(long id, String username, String title, String type, String reservationDate) {
        Specification<Reservation> idEquals = ReservationSpecification.idEquals(id);
        Specification<Reservation> usernameLike = ReservationSpecification.usernameLike(username);
        Specification<Reservation> titleLike = ReservationSpecification.titleLike(title);
        Specification<Reservation> dateEquals = ReservationSpecification.dateEquals(DateUtil.parseDB(reservationDate));
        Specification<Reservation> typeEquals = ReservationSpecification.typeEquals(type);

        Specification<Reservation> spec = Specification.where(idEquals).and(usernameLike).and(titleLike).and(dateEquals)
                                                       .and(typeEquals);

        return reservationRepository.findAll(spec);
    }

    /**
     * Check if a user can borrow an item
     * @param user  The user who wants to borrow an item
     * @param item  The item the user wants to borrow
     * @return      True if the user can borrow the item, false otherwise
     */
    // Visibility is set to public instead of private because Mockito doesn't support private methods mocking
    public boolean checkReservationLimits(User user, Item item) {
        // Admin users can borrow as many items they want
        if (!user.isAdmin()) {
            // Membership duration in years
            long membershipDuration = ChronoUnit.YEARS.between(user.getRegistrationDate(), LocalDate.now());
            // If the user is older than 12, then we consider it as adult
            boolean isAdult = ChronoUnit.YEARS.between(user.getBirthday(), LocalDate.now()) >= 12;
            int nbBorrowed = 0;

            if (item.getItemType().equals("BOOK")) {
                // Get the number of books borrowed by the user
                for (Reservation r : user.getReservations()) {
                    if (r.getItemType().equals("BOOK"))
                        nbBorrowed++;
                }
                return checkBookReservationLimits(membershipDuration, isAdult, nbBorrowed);
            } else if (item.getItemType().equals("DVD")) {
                // Get the number of DVDs borrowed by the user
                for (Reservation r : user.getReservations()) {
                    if (r.getItemType().equals("DVD"))
                        nbBorrowed++;
                }
                return checkDVDReservationLimits(membershipDuration, isAdult, nbBorrowed);
            }
        }
        return true;
    }

    /**
     * Check if a user can borrow a book
     * @param membershipDuration    The number of years since the registration date of the user
     * @param isAdult               True if the user is older than 12, false otherwise
     * @param nbBorrowed            The number of books already borrowed by the user
     * @return                      True if the user can borrow a book, false otherwise
     */
    // Visibility is set to public instead of private because Mockito doesn't support private methods mocking
    public boolean checkBookReservationLimits(long membershipDuration, boolean isAdult, int nbBorrowed) {
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
        else
            return nbBorrowed < 5;
    }

    /**
     * Check if a user can borrow a DVD
     * @param membershipDuration    The number of years since the registration date of the user
     * @param isAdult               True if the user is older than 12, false otherwise
     * @param nbBorrowed            The number of DVDs already borrowed by the user
     * @return                      True if the user can borrow a DVD, false otherwise
     */
    // Visibility is set to protected public of private because Mockito doesn't support private methods mocking
    public boolean checkDVDReservationLimits(long membershipDuration, boolean isAdult, int nbBorrowed) {
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
        else
            return false;
    }

}
