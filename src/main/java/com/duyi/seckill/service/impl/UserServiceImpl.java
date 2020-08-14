package com.duyi.seckill.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.duyi.seckill.dao.UserDao;
import com.duyi.seckill.domain.User;
import com.duyi.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getUser(Integer id) {
        return userDao.getUser(id);
    }

    @Override
    public void addUser(User user) {
        userDao.addUser(user);
    }

    @Override
    public boolean login(String phone,String password) {
        User dbUser = userDao.getUserByPhone(phone);
        //判断用户是否存在
        if (dbUser == null){
            return false;
        }
        //电话不想等
        if (!dbUser.getPhone().equals(phone)){
            return false;
        }
        //密码不想等
        if (!dbUser.getPassword().equals(DigestUtil.md5Hex(password))){
            return false;
        }
        return true;
    }
}
