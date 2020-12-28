package org.libmanager.server.service.impl;

import java.util.Optional;

import org.libmanager.server.entity.User;
import org.libmanager.server.model.Response;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.service.UserService;
import org.libmanager.server.specification.UserSpecification;
import org.libmanager.server.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    public User get(String username) {
        Optional<User> userToFind = userRepository.findById(username);
        return userToFind.orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public Response<Boolean> add(
            String username,
            String firstName,
            String lastName,
            String address,
            String email,
            String birthday,
            String password
    ) {
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
     * {@inheritDoc}
     */
    public boolean edit(
            String username,
            String firstName,
            String lastName,
            String address,
            String email,
            String birthday
    ) {
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
     * {@inheritDoc}
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
