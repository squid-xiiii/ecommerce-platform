package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> findByUserName(String userName);
    List<User> findAllUsers();
    void deleteUser(String id);

    User updateUser(User user);
}