package com.duyi.seckill.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private Integer id;
    private String phone;
    private String password;

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public User(Integer id, String phone, String password) {
        this.id = id;
        this.phone = phone;
        this.password = password;
    }
}
