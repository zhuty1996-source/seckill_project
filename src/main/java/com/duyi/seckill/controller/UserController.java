package com.duyi.seckill.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.duyi.seckill.dao.UserDao;
import com.duyi.seckill.domain.User;
import com.duyi.seckill.exception.UserException;
import com.duyi.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/seckill")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/index")
    public String index() {
        return "login";
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public User getUser(Integer id){
        User user = userService.getUser(id);
        return user;
    }

    @RequestMapping("/getUser/{uid}")
    @ResponseBody
    public User getUser2(@PathVariable("uid") int uid){
        return userService.getUser(uid);
    }
    //用户登录
    @RequestMapping(value = "/login")
    public String login(String phone, String password, HttpSession session){
        System.out.println(phone+"--"+password);

        if (StrUtil.isEmpty(phone) || phone.length() != 11) {
            throw new UserException("手机号不正确");
        }
        if (StrUtil.isEmpty(password)) {
            throw new UserException("密码不正确");
        }
        if (userService.login(phone,password)) {
            // 返回jsp页面
            // 前缀 --> name="prefix" value="/WEB-INF/jsp/"
            // 后缀 --> name="suffix" value=".jsp"
            // 默认页面的位置：/WEB-INF/jsp/seckill_list.jsp
            Integer userId = userDao.getUserByPhone(phone).getId();
            session.setAttribute("user",new User(userId,phone,password));
            return "forward:/seckill/seckillItemList";
        }

        return "regist";
    }

    //用户注册
    @RequestMapping(value = "/regist")
    public String regist(String phone,String password1,String password2){

        // 前段是JS验证，不可靠，可以略过
        // 后端验证，验证参数的合法性
        if (StrUtil.isEmpty(phone) || phone.length() != 11) {
            throw new UserException("手机号不正确");
        }
        if (StrUtil.isEmpty(password1) || StrUtil.isEmpty(password2) || !password1.equals(password2)) {
            throw new UserException("密码不一致");
        }

        User user = new User();
        user.setPhone(phone);
        user.setPassword(DigestUtil.md5Hex(password1));
        userService.addUser(user);
        return "login";
    }

}
