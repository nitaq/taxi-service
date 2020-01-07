package com.internship.amazingtaxiservice.taxiservice.service;

import com.internship.amazingtaxiservice.taxiservice.model.Role;
import com.internship.amazingtaxiservice.taxiservice.model.User;
import com.internship.amazingtaxiservice.taxiservice.model.UserDto;
import com.internship.amazingtaxiservice.taxiservice.model.*;
import com.internship.amazingtaxiservice.taxiservice.repository.*;
import com.internship.amazingtaxiservice.taxiservice.config.JwtTokenProvider;
import com.internship.amazingtaxiservice.taxiservice.rsql.jpa.JpaCriteriaQueryVisitor;
import com.internship.amazingtaxiservice.taxiservice.utils.*;
import com.internship.amazingtaxiservice.taxiservice.utils.InvalidTokenException;
import com.internship.amazingtaxiservice.taxiservice.utils.UserDoesNotExistException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import com.internship.amazingtaxiservice.taxiservice.utils.PasswordValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.*;

import static com.internship.amazingtaxiservice.taxiservice.utils.Validation.isPasswordValid;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private RoleRepository roleRepository;

    private MailService mailService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    private EntityManager entityManager;


    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder,MailService mailService,EntityManager entityManager) {
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.entityManager = entityManager;
    }


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }


    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Override
    public User findById(int theId) {
        Optional<User> result = userRepository.findById(theId);
        User theUser = null;

        if (result.isPresent()) {
            theUser = result.get();
        } else {
            throw new EntryNotFoundException("User");
        }
        return theUser;
    }


    @Override
    public User save(User theUser) {
        User user = userRepository.findByUsername(theUser.getUsername());
        if (user != null) {
            throw new LoginAlreadyUsedException();
        } else {
            String token = UUID.randomUUID().toString();
            long validityInMilliseconds = 3600000 * 24; // 1h
            Date now = new Date();
            Date validity = new Date(now.getTime() + validityInMilliseconds);

            user.setToken(token);
            user.setExpiryDate(validity);

            userRepository.save(theUser);
            return user;
        }
    }


    @Override
    public void deleteById(int theId) {
        userRepository.deleteById(theId);
    }


    @Override
    public User saveUserDto(UserDto userDto) {

        if (!isPasswordValid(userDto.getPassword())) {
            throw new PasswordValidationException();
        }

        User user1 = userRepository.findByUsername(userDto.getUsername());

        if (user1 != null) {
            throw new LoginAlreadyUsedException();
        } else {
            User user = new User();
            Optional<Role> role = roleRepository.findById(2);
            user.setRole(role.get());
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setUsername(userDto.getUsername());

            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setPhone(userDto.getPhone());

            String token = UUID.randomUUID().toString();
            long validityInMilliseconds = 3600000 * 24; // 1h
            Date now = new Date();
            Date validity = new Date(now.getTime() + validityInMilliseconds);

            user.setToken(token);

            user.setExpiryDate(validity);
            userRepository.save(user);

            return user;
        }
    }



    @Override
    public void activateUser(String activationToken) {
        User user = userRepository.findByToken(activationToken);

        Date todaysDate = new Date();
        if (todaysDate.before(user.getExpiryDate())) {

            user.setEnabled(true);
            user.setExpiryDate(null);
            user.setToken(null);
            userRepository.save(user);
        } else {
            throw new InvalidTokenException("Invalid token");
        }
    }


    @Override
    public void createPasswordResetTokenForUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntryNotFoundException("User");
        }
        String token = UUID.randomUUID().toString();

        long validityInMilliseconds = 3600000 * 24; // 24h
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        user.setResetExpiryDate(validity);
        user.setResetToken(token);
        userRepository.save(user);

        mailService.constructResetTokenEmail("http://localhost:4200/fe-forgotpassword", token, user);
    }


    @Override
    public void resendActivationEmail(String username) {

        User user = userRepository.findByUsername(username);

        String token = UUID.randomUUID().toString();

        long validityInMilliseconds = 3600000 * 24; // 1h
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        user.setToken(token);
        user.setExpiryDate(validity);
        mailService.sendVerificationMail("http://localhost:8080", user);
    }


    public void changeUserPassword(PasswordDto passwordDto) {
        User user = userRepository.findByResetToken(passwordDto.getResetToken());

        // token validation
        Date todaysDate = new Date();
        if (todaysDate.before(user.getResetExpiryDate())) {
            if (isPasswordValid(passwordDto.getNewPassword())) {
                user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
                user.setResetToken(null);
                user.setResetExpiryDate(null);
                userRepository.save(user);
            } else {
                throw new InvalidPasswordOrUsernameException();
            }
        } else {
            throw new InvalidTokenException("Expired token");
        }
    }


    public boolean encodedPasswordsMatch(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public Object getUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserDoesNotExistException();
        } else {
            return user;
        }
    }


    @Override
    public void changeUserRole(int userId, int roleId) {
        User user = null;

        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        if (optionalUser.isPresent() && optionalRole.isPresent()) {
            user = optionalUser.get();
            user.setRole(optionalRole.get());
            userRepository.save(user);
        } else {
            throw new UserDoesNotExistException();
        }
    }


    // CHANGE PASSWORD when logged in
    @Override
    public void updatePassword(UpdatePasswordDto updatePasswordDto) {

        User user = userService.findById(updatePasswordDto.getUserId());

        if (!userService.encodedPasswordsMatch(user, updatePasswordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }

        if (!Validation.passwordMatch(updatePasswordDto.getNewPassword(), updatePasswordDto.getConfirmPassword())) {
            throw new PasswordMatchException();
        }

        if (isPasswordValid(updatePasswordDto.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new PasswordValidationException();
        }
    }




    @Override
    public void requestChangeUserEmail(int userId, String email) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = null;

        String token = UUID.randomUUID().toString();
        long validityInMilliseconds = 3600000 * 24; // 1h
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setToken(token);
            user.setExpiryDate(validity);

            userRepository.save(user);

            mailService.constructChangeEmailLink("http://localhost:8080", token, email, user);
        } else {
            throw new UserDoesNotExistException();
        }
    }


    @Override
    public void changeEmail(String token, String newEmail) {
        User user = userRepository.findByToken(token);
        user.setUsername(newEmail);
        user.setToken(null);
        user.setExpiryDate(null);
        userRepository.save(user);
    }


    @Override
    public List<User> searchByQuery(String queryString) {
        RSQLVisitor<CriteriaQuery<User>, EntityManager> visitor = new JpaCriteriaQueryVisitor<>();
        CriteriaQuery<User> query;
        query = getCriteriaQuery(queryString, visitor);
        List<User> resultList = entityManager.createQuery(query).getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Collections.emptyList();
        }
        return resultList;
    }


    @Override
    public <T> CriteriaQuery<T> getCriteriaQuery(String queryString, RSQLVisitor<CriteriaQuery<T>, EntityManager> visitor) {
        Node rootNode;
        CriteriaQuery<T> query;
        try {
            rootNode = new RSQLParser().parse(queryString);
            query = rootNode.accept(visitor, entityManager);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return query;
    }


    @Override
    public User findUserByToken(String bearerToken) {
        User user = userRepository.findByUsername(jwtTokenProvider.subjectClaim(bearerToken));
        return user;
    }

}