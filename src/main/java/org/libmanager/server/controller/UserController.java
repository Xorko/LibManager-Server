package org.libmanager.server.controller;

import org.libmanager.server.entity.User;
import org.libmanager.server.model.Response;
import org.libmanager.server.service.UserService;
import org.libmanager.server.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Add a user
     * @param token         The token of the user (must be admin)
     * @param username      The username of the user
     * @param firstName     The firstname of the user
     * @param lastName      The lastname of the user
     * @param address       The address of the user
     * @param email         The email of the user
     * @param birthday      The birthday of the user
     * @param password      The password of the user
     * @return              A JSON response with OK code and true if the user was added, the error and false otherwise
     */
    @PostMapping(path = "/add")
    public @ResponseBody
    Response<Boolean> addNewUser(
            @RequestParam String token,
            @RequestParam String username,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String address,
            @RequestParam String email,
            @RequestParam String birthday,
            @RequestParam String password) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token))
                return userService.add(username, firstName, lastName, address, email, birthday, password);
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, false);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

    /**
     * Edit a user
     * @param token         The token of the user (must be admin)
     * @param username      The username of the user
     * @param firstName     The firstname of the user
     * @param lastName      The lastname of the user
     * @param address       The address of the user
     * @param email         The email of the user
     * @param birthday      The birthday of the user
     * @return              A JSON response with OK code and true if the user was edited, the error and false otherwise
     */
    @PostMapping(path = "/edit/{username}")
    public @ResponseBody
    Response<Boolean> editUser(
            @PathVariable String username,
            @RequestParam String token,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String address,
            @RequestParam String email,
            @RequestParam String birthday) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token))
                return new Response<>(Response.Code.OK, userService.edit(username, firstName, lastName, address, email, birthday));
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, true);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

    /**
     * Get all the users
     * @param token The token of the user (must be admin)
     * @return      A JSON response with OK code and an array with all users if the token is valid, the error and null otherwise
     */
    @PostMapping(path = "/all")
    public @ResponseBody
    Response<Iterable<User>> getAllUsers(@RequestParam String token) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token))
                return new Response<>(Response.Code.OK, userService.getAll());
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, null);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, null);
    }

    /**
     * Get a user by its username
     * @param token     The token of the user (must be admin)
     * @param username  The username of the user to get
     * @return          A JSON response with OK code and the user or the error and null if not found
     */
    @PostMapping(path = "/get/{username}")
    public @ResponseBody
    Response<User> getUser(
            @RequestParam String token,
            @PathVariable String username
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token)) {
                User foundUser = userService.get(username);
                if (foundUser == null)
                    return new Response<>(Response.Code.NOT_FOUND, null);
                return new Response<>(Response.Code.OK, foundUser);
            }
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, null);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, null);
    }

    /**
     * Search users
     * @param token             The token of the user (must be admin)
     * @param username          The username of the user
     * @param firstName         The firstname of the user
     * @param lastName          The lastname of the user
     * @param address           The address of the user
     * @param email             The email of the user
     * @param birthday          The birthday of the user
     * @param registrationDate  The registration date of the user
     * @return                  A JSON response with OK code and an array with all users found if the token is valid, the error and null otherwise
     */
    @PostMapping(path = "/search")
    public @ResponseBody
    Response<Iterable<User>> searchUsers(
            @RequestParam String token,
            @RequestParam(defaultValue = "null") String username,
            @RequestParam(defaultValue = "null") String firstName,
            @RequestParam(defaultValue = "null") String lastName,
            @RequestParam(defaultValue = "null") String address,
            @RequestParam(defaultValue = "null") String email,
            @RequestParam(defaultValue = "null") String birthday,
            @RequestParam(defaultValue = "null") String registrationDate
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token)) {
                Iterable<User> users;
                // If no argument is given, return all users
                if (username.equals("null") && firstName.equals("null") && lastName.equals("null") && address.equals("null") &&
                        email.equals("null") && birthday.equals("null") && registrationDate.equals("null"))
                    users = userService.getAll();
                else
                    users = userService.search(username, firstName, lastName, address, email, birthday, registrationDate);
                return new Response<>(Response.Code.OK, users);
            }
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, null);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, null);
    }

    /**
     * Delete a user
     * @param token     The token of the user (must be admin)
     * @param username  The username of the user to delete
     * @return          A JSON response with OK code and true if the user was successfully deleted, the error and false otherwise
     */
    @PostMapping(path = "/delete/{username}")
    public @ResponseBody
    Response<Boolean> deleteUser(
            @RequestParam String token,
            @PathVariable String username
    ) {
        if (TokenUtil.isValid(token)) {
            if (TokenUtil.isAdmin(token))
                return new Response<>(Response.Code.OK, userService.delete(username));
            return new Response<>(Response.Code.INSUFFICIENT_PERMISSIONS, false);
        }
        return new Response<>(Response.Code.INVALID_TOKEN, false);
    }

}
