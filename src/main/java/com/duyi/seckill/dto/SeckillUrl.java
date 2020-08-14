package com.duyi.seckill.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SeckillUrl {

    //true: seckillid有对应的商品
    //false: seckillid没有对应的商品
    private boolean enable;
    private String md5;
    private int seckillId;
    private long now;
    private long start;
    private long end;

    public SeckillUrl(boolean enable, String md5, int seckillId, long now, long start, long end) {
        this.enable = enable;
        this.md5 = md5;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public SeckillUrl(boolean enable, int seckillId) {
        this.enable = enable;
        this.seckillId = seckillId;
    }

    public SeckillUrl(boolean enable, int seckillId, long now, long start, long end) {
        this.enable = enable;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }
}
