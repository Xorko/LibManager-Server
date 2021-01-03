package org.libmanager.server.service.impl;

import java.util.Optional;

import org.libmanager.server.entity.User;
import org.libmanager.server.response.Response;
import org.libmanager.server.repository.UserRepository;
import org.libmanager.server.service.UserService;
import org.libmanager.server.specification.UserSpecification;
import org.libmanager.server.util.DateUtil;
import org.mindrot.jbcrypt.BCrypt;
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
        Specification<User> usernameLike = UserSpecification.usernameLike(username);
        Specification<User> firstNameLike = UserSpecification.firstNameLike(firstName);
        Specification<User> lastNameLike = UserSpecification.lastNameLike(lastName);
        Specification<User> emailLike = UserSpecification.emailLike(email);
        Specification<User> addressLike = UserSpecification.addressLike(address);
        Specification<User> birthdayEquals = UserSpecification.birthdayEquals(DateUtil.parseDB(birthday));
        Specification<User> registrationDateEquals = UserSpecification.registrationDateEquals(DateUtil.parseDB(registrationDate));


        Specification<User> spec = Specification.where(usernameLike).and(firstNameLike).and(lastNameLike).and(emailLike)
                                                .and(addressLike).and(birthdayEquals).and(registrationDateEquals);

        return userRepository.findAll(spec);
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(
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
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAddress(address);
            user.setEmail(email);
            user.setBirthday(DateUtil.parseDB(birthday));
            userRepository.save(user);
            return true;
        }
        return false;
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
    public Response<Boolean> delete(String username) {
        Optional<User> foundUser = userRepository.findById(username);
        if (foundUser.isEmpty())
            return new Response<>(Response.Code.NOT_FOUND, false);
        User userToDelete = foundUser.get();
        if (userToDelete.isAdmin())
            return new Response<>(Response.Code.FORBIDDEN, false);
        userRepository.delete(userToDelete);
        return new Response<>(Response.Code.OK, true);
    }

    /**
     * {@inheritDoc}
     */
    public boolean usernameIsUnique(String username) {
        return get(username) == null;
    }

}
