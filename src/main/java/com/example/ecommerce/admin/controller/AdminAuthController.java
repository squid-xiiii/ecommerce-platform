package com.example.ecommerce.admin.controller;

import com.example.ecommerce.admin.entity.Admin;
import com.example.ecommerce.admin.repository.AdminRepository;
import com.example.ecommerce.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        System.out.println("管理员登录请求: username=" + username + ", password=" + password);

        Optional<Admin> adminOpt = adminRepository.findByUsername(username);

        if (adminOpt.isEmpty()) {
            System.out.println("管理员不存在: " + username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "管理员不存在"));
        }

        Admin admin = adminOpt.get();
        System.out.println("找到管理员: " + admin.getUsername() + ", 密码: " + admin.getPassword());

        if (!admin.getPassword().equals(password)) {
            System.out.println("密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "密码错误"));
        }

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("id", admin.getId());
        adminInfo.put("username", admin.getUsername());
        adminInfo.put("nickname", admin.getNickname() != null ? admin.getNickname() : admin.getUsername());
        result.put("admin", adminInfo);
        result.put("role", "admin");

        System.out.println("管理员登录成功: " + username);
        return ResponseEntity.ok(ApiResponse.success("登录成功", result));
    }
}