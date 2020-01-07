package com.internship.amazingtaxiservice.taxiservice.service;

import com.internship.amazingtaxiservice.taxiservice.model.*;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
@Service
public interface UserService {

    Page<User> findAll(Pageable pageable);

    User findById(int theId);

    User save(User theUser);

    void deleteById(int theId);

    User  saveUserDto(UserDto userDto);

    User findByUsername(String username);

    void activateUser(String activationToken);

    void resendActivationEmail(String email);

    void createPasswordResetTokenForUser(String username);

    void changeUserPassword(PasswordDto newPassword);

    void updatePassword(UpdatePasswordDto updatePasswordDto);

    boolean encodedPasswordsMatch(User user, String oldPassword);

    void requestChangeUserEmail(int id, String email);

    void changeEmail(String token, String newEmail);


    Object getUserProfile(String username);

    void changeUserRole(int userId, int roleId);

    List<User> searchByQuery(String queryString);

    <T> CriteriaQuery<T> getCriteriaQuery(String queryString, RSQLVisitor<CriteriaQuery<T>, EntityManager> visitor);

    User findUserByToken(String token);
}