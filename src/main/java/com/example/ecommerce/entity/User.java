package com.example.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userName;
    private String password;
    private String nickName;
    private String name;
    private Integer age;
    private String sex;          // "男"/"女"
    private String school;
    private String address;
    private String codeNum;      // 身份证
    private String phone;
    private Date createdAt;
}
