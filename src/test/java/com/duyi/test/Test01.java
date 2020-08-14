package com.duyi.test;

import cn.hutool.crypto.digest.DigestUtil;
import com.duyi.seckill.dao.RedisDao;
import com.duyi.seckill.dao.SeckillItemDao;
import com.duyi.seckill.dao.SeckillOrderDao;
import com.duyi.seckill.domain.SeckillItem;
import com.duyi.seckill.domain.SeckillOrder;
import com.duyi.seckill.domain.User;
import com.duyi.seckill.dto.SeckillUrl;
import com.duyi.seckill.service.SeckillItemService;
import com.duyi.seckill.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.List;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/spring-*.xml")
public class Test01 {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private SeckillItemService seckillItemService;

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Autowired
    private SeckillItemDao seckillItemDao;

    @Autowired
    private RedisDao redisDao;
//    @Test
//    public void test01(){
//        JedisPool jedisPool = new JedisPool();
//
//        Jedis jedis = jedisPool.getResource();
//        //String value = jedis.get("name");
//        List<String> db = jedis.lrange("db", 0, 10);
//        System.out.println(db);
//    }

    @Test
    public void test02(){
        System.out.println(redisTemplate);
    }

    @Test
    public void test03(){
        System.out.println(DigestUtil.md5Hex("123"));
        User user = new User();
        user.setPhone("138888");
        user.setPassword(DigestUtil.md5Hex("123123"));
        userService.addUser(user);
    }

    @Test
    public void test04(){
        boolean b = userService.login("","");
        System.out.println(b);
    }

    @Test
    public void test05(){
        List<SeckillItem> seckillItemList = seckillItemService.getAllItem();
        System.out.println(seckillItemList);
    }
    @Test
    public void test06(){
        SeckillItem item = seckillItemService.getItemById(1);
        System.out.println(item);
    }
    @Test
    public void test07(){
        SeckillUrl seckillUrl = seckillItemService.getSeckillUrlById(1);
        System.out.println(seckillUrl);
    }
    @Test
    public void test08(){
//        SeckillOrder seckillOrder = new SeckillOrder();
//        seckillOrder.setSeckillItemId(99);
//        seckillOrder.setUserId(11111);
//        seckillOrder.setState(1);
//        seckillOrder.setCreateTime(new Date());
//        seckillOrderDao.insert(seckillOrder);
//        seckillItemDao.updateStock(1);
        //System.out.println(seckillOrderDao.getAllNotPaid());

        redisDao.stockAdd("stock_1");

    }
}
