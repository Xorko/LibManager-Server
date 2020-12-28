package org.libmanager.server.service;

import java.util.Optional;

import org.libmanager.server.entity.User;
import org.libmanager.server.model.Response;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.specification.UserSpecification;
import org.libmanager.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get a user
     * @param username  The username of the user to get
     * @return          The user if found, null otherwise
     */
    public User get(String username) {
        Optional<User> userToFind = userRepository.findById(username);
        return userToFind.orElse(null);
    }

    /**
     * Get all the users
     * @return  All users in the database
     */
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Search a user in
     * @param username          The username to find
     * @param firstName         The firstname to find
     * @param lastName          The lastname to find
     * @param address           The address to find
     * @param email             The email to find
     * @param birthday          The birthday to find
     * @param registrationDate  The registration date to find
     * @return                  An iterable object with all found users
     */
    public Iterable<User> search(
            String username,
            String firstName,
            String lastName,
            String address,
            String email,
            String birthday,
            String registrationDate
    ) {
        User filter = new User();
        filter.setUsername('%' + username + '%');
        filter.setFirstName('%' + firstName + '%');
        filter.setLastName('%' + lastName + '%');
        filter.setEmail('%' + email + '%');
        filter.setAddress('%' + address + '%');
        filter.setBirthday(DateUtil.parseDB(birthday));
        filter.setRegistrationDate(DateUtil.parseDB(registrationDate));

        Specification<User> spec = new UserSpecification(filter);

        return userRepository.findAll(spec);
    }

    /**
     * Add a user
     * @param username      The username of the user
     * @param firstName     The firstname of the user
     * @param lastName      The lastname of the user
     * @param address       The address of the user
     * @param email         The email of the user
     * @param birthday      The birthday of the user
     * @param password      The password of the user
     */
    public Response<Boolean> add(
            String username,
            String firstName,
            String lastName,
            String address,
            String email,
            String birthday,
            String password) {
        if (userRepository.count() + 1 <= 2000) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAddress(address);
            user.setEmail(email);
            user.setBirthday(DateUtil.parseDB(birthday));
            userRepository.save(user);
            return new Response<>(Response.Code.OK, true);
        }
        return new Response<>(Response.Code.MAX_USERS_REACHED, false);
    }

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
    public boolean edit(
            String username,
            String firstName,
            String lastName,
            String address,
            String email,
            String birthday) {
        Optional<User> foundUser = userRepository.findById(username);
        if (foundUser.isEmpty())
            return false;
        User userToEdit = foundUser.get();
        userToEdit.setFirstName(firstName);
        userToEdit.setLastName(lastName);
        userToEdit.setEmail(email);
        userToEdit.setAddress(address);
        userToEdit.setBirthday(DateUtil.parseDB(birthday));
        userRepository.save(userToEdit);
        return true;
    }

    /**
     * Delete a user
     * @param username  The username of the user to delete
     * @return          A JSON response with success = true if the user was added, false otherwise
     */
    public boolean delete(String username) {
        Optional<User> foundUser = userRepository.findById(username);
        if (foundUser.isEmpty())
            return false;
        User userToDelete = foundUser.get();
        userRepository.delete(userToDelete);
        return true;
    }

}
