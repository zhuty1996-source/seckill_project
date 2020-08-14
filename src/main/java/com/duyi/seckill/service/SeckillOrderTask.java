package com.duyi.seckill.service;


import cn.hutool.core.util.ObjectUtil;
import com.duyi.seckill.dao.RedisDao;
import com.duyi.seckill.dao.SeckillItemDao;
import com.duyi.seckill.dao.SeckillOrderDao;
import com.duyi.seckill.domain.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@EnableScheduling

public class SeckillOrderTask {

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Autowired
    private SeckillItemDao seckillItemDao;

    @Autowired
    private RedisDao redisDao;

    @Scheduled(fixedRate = 1 * 1000)
    @Transactional
    public void runsecend(){
        System.out.println("******the task is working******");

        //获取数据库中所有未支付的订单
        List<SeckillOrder> orderList = seckillOrderDao.getAllNotPaid();
        if (ObjectUtil.isEmpty(orderList) || orderList.size() == 0){
            //没有未支付的订单
            return;
        }
        for (SeckillOrder seckillOrder : orderList){
            String orderCode = seckillOrder.getOrderCode();
            //根据返回结果，判断redis订单是否超时
            Object existOrder = redisDao.get("timeout_" + orderCode);
            if (ObjectUtil.isEmpty(existOrder)){
                //订单超时，不可继续支付，更改数据库订单状态
                seckillOrderDao.updateOrderState(seckillOrder);
                //redis库存+1
                redisDao.stockAdd("stock_" + seckillOrder.getSeckillItemId());
                //mysql库存 + 1
                seckillItemDao.stockAdd(seckillOrder.getSeckillItemId());

            }
        }


    }

}
