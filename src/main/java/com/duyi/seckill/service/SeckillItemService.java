package com.duyi.seckill.service;

import com.duyi.seckill.domain.SeckillItem;
import com.duyi.seckill.domain.SeckillOrder;
import com.duyi.seckill.domain.User;
import com.duyi.seckill.dto.SeckillUrl;

import java.util.List;

public interface SeckillItemService {

    List<SeckillItem> getAllItem();

    SeckillItem getItemById(Integer id);

    SeckillUrl getSeckillUrlById(Integer id);

    boolean verifyMD5(Integer id, String md5);

    boolean executeSeckill(User user, Integer seckillId);

    SeckillOrder placeOrder(User user, Integer seckillId);

    void pay(String orderCode);

    SeckillOrder getSeckillOrder(String orderCode);
}
