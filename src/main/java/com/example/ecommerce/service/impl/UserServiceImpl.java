package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Date());
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(User user) {
        // 保留原密码（如果新密码为空）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            Optional<User> existing = userRepository.findById(user.getId());
            existing.ifPresent(u -> user.setPassword(u.getPassword()));
        }
        return userRepository.save(user);
    }
}