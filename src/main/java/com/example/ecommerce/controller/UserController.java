package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.SystemLogService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 创建用户
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userService.findByUserName(user.getUserName()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "用户名已存在"));
        }
        User saved = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("注册成功", saved));
    }

    @Autowired
    private SystemLogService systemLogService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> params) {
        String userName = params.get("userName");
        String password = params.get("password");

        return (ResponseEntity<ApiResponse<Map<String, Object>>>) userService.findByUserName(userName)
                .map(user -> {
                    if (user.getPassword() != null && user.getPassword().equals(password)) {
                        systemLogService.info("用户登录成功: " + userName);
                        Map<String, Object> result = new HashMap<>();
                        result.put("user", user);
                        result.put("role", "user");
                        return ResponseEntity.ok(ApiResponse.success("登录成功", result));
                    } else {
                        systemLogService.warn("用户登录失败(密码错误): " + userName);
                        return ResponseEntity.status(401).body(ApiResponse.error(401, "密码错误"));
                    }
                })
                .orElseGet(() -> {
                    systemLogService.warn("用户登录失败(用户不存在): " + userName);
                    return ResponseEntity.status(404).body(ApiResponse.error(404, "用户不存在"));
                });
    }

    // 根据用户名查询用户
    @GetMapping("/{userName}")
    public ResponseEntity<ApiResponse<User>> getUserByUserName(@PathVariable String userName) {
        return userService.findByUserName(userName)
                .map(user -> {
                    // 返回时隐藏密码
                    user.setPassword(null);
                    return ResponseEntity.ok(ApiResponse.success(user));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "用户不存在")));
    }

    // 查询所有用户
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        // 隐藏所有用户的密码
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.updateUser(user);
        updated.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}