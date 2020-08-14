package com.duyi.seckill.dao;

import com.duyi.seckill.domain.SeckillOrder;

import java.util.List;

public interface SeckillOrderDao {

    void insert(SeckillOrder seckillOrder);

    List<SeckillOrder> getAllNotPaid();

    void updateOrderState(SeckillOrder seckillOrder);

    void pay(String orderCode);

    SeckillOrder getSeckillOrder(String orderCode);

}
