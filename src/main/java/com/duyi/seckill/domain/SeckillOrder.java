package com.duyi.seckill.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillOrder {

    private int id;
    private String orderCode;
    private int seckillItemId;
    private int userId;

    //1 下单成功 未支付
    //TODO
    //2 已支付
    //4 订单超时
    private int state;
    private Date createTime;

}
