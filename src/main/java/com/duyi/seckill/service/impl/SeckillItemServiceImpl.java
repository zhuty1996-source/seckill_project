package com.duyi.seckill.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.duyi.seckill.dao.RedisDao;
import com.duyi.seckill.dao.SeckillItemDao;
import com.duyi.seckill.dao.SeckillOrderDao;
import com.duyi.seckill.domain.SeckillItem;
import com.duyi.seckill.domain.SeckillOrder;
import com.duyi.seckill.domain.User;
import com.duyi.seckill.dto.SeckillUrl;
import com.duyi.seckill.service.SeckillItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SeckillItemServiceImpl implements SeckillItemService {

    @Autowired
    private SeckillItemDao seckillItemDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Override
    public List<SeckillItem> getAllItem() {
        return seckillItemDao.getAllItem();
    }

    @Override
    public SeckillItem getItemById(Integer id) {
        return seckillItemDao.getItemById(id);
    }

    @Override
    public SeckillUrl getSeckillUrlById(Integer id) {

        //获取SeckillItem 从数据库中获取
        //SeckillItem item = seckillItemDao.getItemById(id);
        //使用redis缓存 获取SeckillItem对象
        SeckillItem item = (SeckillItem) redisDao.get(String.valueOf(id));

        if (ObjectUtil.isEmpty(item)){

            item = seckillItemDao.getItemById(id);
            if (ObjectUtil.isEmpty(item)){
                //数据库中没有id对应的商品
                return new SeckillUrl(false,id);
            }

            //保存到redis中
            redisDao.set(String.valueOf(id),item);

            //同时把库存更新到redis中
            redisDao.set("stock_" + id,item.getNumber());
        }
        Date startTime = item.getStartTime();
        Date endTime = item.getEndTime();
        Date nowTime = new Date();

        //服务器返回秒杀地址 意味着前端JS的00已经结束，可以进行抢购了
        //返回SeckillUrl的条件
        //1、当前时间小于秒杀商品的开始时间，或大于结束时间，不是正常获取秒杀地址的请求
        if (nowTime.getTime() < startTime.getTime()
        || nowTime.getTime() > endTime.getTime()){
            return new SeckillUrl(false,id,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }

        //正常返回秒杀地址
        String md5 = this.seckillUrlmd5(id);

        return new SeckillUrl(true,md5,id,nowTime.getTime(),startTime.getTime(),endTime.getTime());
    }

    @Override
    public boolean verifyMD5(Integer id, String md5) {
        String sMD5 = seckillUrlmd5(id);
        if (StrUtil.isEmpty(md5) || !md5.equals(sMD5)){
            return false;
        }
        return true;
    }

    @Override
    public boolean executeSeckill(User user, Integer seckillId) {

        //存值 key-->phone_seckillId,value--seckillId
        String key = user.getPhone() + "_" + seckillId;

        Integer mSeckillId = (Integer) redisDao.get(key);

        if (!ObjectUtil.isEmpty(mSeckillId)){
            //已经请求过了，5分钟内部允许同一个用户第二次下单
            return false;
        }

        //redis保存用户下单缓存，key-->phone_seckillId,value-->seckillId
        //设置超时时间 5 minutes
        redisDao.setex(key,seckillId,60*5);


        //1、先去redis里查库存
        //TODO 减库存
        // -1 库存不足
        // -2 不存在
        // 整数是正常操作 减库存成功
        Integer result = redisDao.stockDecr("stock_" + seckillId);
        if (ObjectUtil.isEmpty(result)){
            return false;
        }
        if (result == -1){
            //-1 库存不足
            return false;
        }
        if (result == -2){
            //-2 不存在
            return false;
        }
        return true;

    }

    //更新库存 生成订单保存
    @Transactional
    @Override
    public SeckillOrder placeOrder(User user,Integer seckillId){
        //更新seckillitem数据库中的库存 result-->number

        seckillItemDao.updateStock(seckillId);

        //2、判断库存是否可以下单（大于0）
        /**
         * {
         * lua脚本：
         * 1、获取key对应的value
         * 2、if判断是否库存大于0
         * 3、更新redia中的库存-1
         * 4、string类型转换
         * }
         */
        //3、商品表、订单表，同时需要操作同一事务，需保持操作的一致性
        //4、减库存，下订单。




        String simpleUUID = IdUtil.simpleUUID();
        //TODO
        // 生成秒杀订单并保存
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setSeckillItemId(seckillId);
        seckillOrder.setOrderCode(simpleUUID);
        seckillOrder.setUserId(user.getId());
        seckillOrder.setState(1);
        seckillOrder.setCreateTime(new Date());
        seckillOrderDao.insert(seckillOrder);

        redisDao.setex("timeout_" + seckillOrder.getOrderCode() , seckillOrder , 60 * 5);

        return seckillOrder;
    }

    /**
     * 支付成功
     * @param orderCode
     */
    @Override
    public void pay(String orderCode) {
        seckillOrderDao.pay(orderCode);
    }

    @Override
    public SeckillOrder getSeckillOrder(String orderCode) {
        return seckillOrderDao.getSeckillOrder(orderCode);
    }

    //MD5混淆
    private static final String mixKey = "dsfasfds$%$513";

    private String seckillUrlmd5(Integer id){
        String source = id + "|"+mixKey;
        String md5URL = DigestUtil.md5Hex(source);
        return md5URL;
    }
}
