package com.duyi.seckill.dao;

import com.duyi.seckill.domain.User;

public interface UserDao {

    User getUser(Integer id);

    void addUser(User user);

    User getUserByPhone(String phone);
}
