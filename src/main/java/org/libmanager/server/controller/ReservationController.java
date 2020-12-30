package org.libmanager.server.controller;

import org.libmanager.server.entity.Reservation;
import org.libmanager.server.response.Response;
import org.libmanager.server.service.ReservationService;
import org.libmanager.server.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * Add a reservation
     * @param token     The token of the user who wants to borrow a book
     * @param itemId    The id of the item the user wants to borrow
     * @return          A JSON response with OK code and true if the reservation was successfully created, the error and
     *                  false otherwise
     */
    @PostMapping(path = "/add")
    public @ResponseBody
    Response<Boolean> addReservation(
            @RequestParam String token,
            @RequestParam long itemId
    ) {
        if (TokenUtil.isValid(token)) {
            String username = TokenUtil.extractUsername(token);
            return reservationService.add(username, itemId);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

    /**
     * Delete a reservation
     * @param token The token of the user (must be admin)
     * @param id    The id of the reservation to delete
     * @return      A JSON response with OK code and true if the reservation was successfully deleted, the error and
     *              false otherwise
     */
    @PostMapping(path = "/delete/{id}")
    public @ResponseBody
    Response<Boolean> deleteReservation(
            @RequestParam String token,
            @PathVariable long id
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token)) {
                return reservationService.delete(id);
            }
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, false);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

    /**
     * Get all reservations
     * @param token The token of the user (must be admin)
     * @return      A JSON response with OK code and all reservations
     */
    @PostMapping(path = "/all")
    public @ResponseBody
    Response<Iterable<Reservation>> getAll(@RequestParam String token) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token))
                return new Response<>(Response.Code.OK, reservationService.getAll());
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, null);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, null);
    }

    /**
     * Get a reservation
     * @param token The token of the user (must be admin)
     * @param id    The id of the reservation to get
     * @return      The reservation if found, null otherwise
     */
    @PostMapping(path = "/get/{id}")
    public @ResponseBody
    Response<Reservation> get(
            @RequestParam String token,
            @PathVariable long id
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token)) {
                Reservation reservation = reservationService.get(id);
                if (reservation != null)
                    return new Response<>(Response.Code.OK, reservation);
                return new Response<>(Response.Code.NOT_FOUND, null);
            }
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, null);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, null);
    }

    @PostMapping(path = "/get_user_reservations")
    public @ResponseBody
    Response<Iterable<Reservation>> getByUser(
            @RequestParam String token
    ) {
        if (TokenUtil.isValid(token)) {
            return new Response<>(Response.Code.OK, reservationService.getByUser(TokenUtil.extractUsername(token)));
        }
        return new Response<>(Response.Code.INVALID_TOKEN, null);
    }

    @PostMapping(path = "/search")
    public @ResponseBody
    Response<Iterable<Reservation>> search(
            @RequestParam(defaultValue = "null") String token,
            @RequestParam(defaultValue = "0") long id,
            @RequestParam(defaultValue = "null") String username,
            @RequestParam(defaultValue = "null") String title,
            @RequestParam(defaultValue = "null") String itemType
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token)) {
                Iterable<Reservation> reservationIterable;
                // If no argument is given, return all reservations
                if (id == 0 && username.equals("null") && title.equals("null") && itemType.equals("null"))
                    reservationIterable = reservationService.getAll();
                else
                    reservationIterable = reservationService.search(id, username, title, itemType);
                return new Response<>(Response.Code.OK, reservationIterable);
            }
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, null);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, null);
    }

}
