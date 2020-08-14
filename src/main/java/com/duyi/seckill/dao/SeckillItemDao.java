package com.duyi.seckill.dao;

import com.duyi.seckill.domain.SeckillItem;

import java.util.List;

public interface SeckillItemDao {

    List<SeckillItem> getAllItem();

    SeckillItem getItemById(Integer id);

    void updateStock(Integer id);

    void stockAdd(Integer id);

}
