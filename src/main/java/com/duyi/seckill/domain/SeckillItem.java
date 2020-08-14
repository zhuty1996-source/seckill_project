package com.duyi.seckill.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillItem {

    private int id;
    private String name;
    private int number;
    //      private BigDecimal price;
    private float price;
    private Date startTime;
    private Date endTime;
    private Date createTime;

}
