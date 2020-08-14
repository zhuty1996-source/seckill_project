package com.duyi.seckill.service;

import com.duyi.seckill.domain.User;

public interface UserService {

    User getUser(Integer id);

    void addUser(User user);

    boolean login(String phone,String password);

}
