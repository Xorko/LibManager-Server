package org.libmanager.server.service;

import org.libmanager.server.entity.Reservation;
import org.libmanager.server.response.Response;

public interface ReservationService {

    /**
     * Add a reservation
     * @param username  The user who wants to borrow an item
     * @param itemId    The id of the item the user wants to borrow
     * @return          A JSON response with true if the reservation was successfully created, the error and false otherwise
     */
    Response<Boolean> add(String username, long itemId);

    /**
     * Delete a reservation
     * @param id    The id of the reservation to delete
     * @return      A JSON response with true if the reservation was successfully deleted, the error and false otherwise
     */
    Response<Boolean> delete(long id);

    /**
     * Get all reservations
     * @return  All reservations in the database
     */
    Iterable<Reservation> getAll();

    /**
     * Get a reservation
     * @param id    The id of the reservation to find
     * @return      The reservation if found, null otherwise
     */
    Reservation get(long id);

    /**
     * Get all reservations of a user
     * @param username The username of the user
     * @return         All reservations of the user
     */
    Iterable<Reservation> getByUser(String username);

    /**
     * Search reservations
     * @param id        The reservation id to find
     * @param username  The username to find
     * @param title     The title to find
     * @return          An iterable object with all found reservations
     */
    Iterable<Reservation> search(long id, String username, String title, String type);
}
