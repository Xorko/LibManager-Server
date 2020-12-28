package org.libmanager.server.service;

import org.libmanager.server.entity.User;
import org.libmanager.server.model.Response;

public interface UserService {

    /**
     * Get a user
     * @param username  The username of the user to get
     * @return          The user if found, null otherwise
     */
    User get(String username);

    /**
     * Get all the users
     * @return  All users in the database
     */
    Iterable<User> getAll();

    /**
     * Search a user
     * @param username          The username to find
     * @param firstName         The firstname to find
     * @param lastName          The lastname to find
     * @param address           The address to find
     * @param email             The email to find
     * @param birthday          The birthday to find
     * @param registrationDate  The registration date to find
     * @return                  An iterable object with all found users
     */
    Iterable<User> search(
            String username,
            String firstName,
            String lastName,
            String address,
            String email,
            String birthday,
            String registrationDate
    );

    /**
     * Add a user
     * @param username      The username of the user
     * @param firstName     The firstname of the user
     * @param lastName      The lastname of the user
     * @param address       The address of the user
     * @param email         The email of the user
     * @param birthday      The birthday of the user
     * @param password      The password of the user
     * @return              A response with OK code and true if the user was successfully added, error and false otherwise
     */
    Response<Boolean> add(
            String username,
            String firstName,
            String lastName,
            String address,
            String email,
            String birthday,
            String password
    );

    /**
     * Edit a user
     * @param username      The username of the user
     * @param firstName     The firstname of the user
     * @param lastName      The lastname of the user
     * @param address       The address of the user
     * @param email         The email of the user
     * @param birthday      The birthday of the user
     * @return              True if the user was edited, false otherwise
     */
    boolean edit(
            String username,
            String firstName,
            String lastName,
            String address,
            String email,
            String birthday
    );

    /**
     * Delete a user
     * @param username  The username of the user to delete
     * @return          A JSON response with success = true if the user was added, false otherwise
     */
    boolean delete(String username);

}
