package com.duyi.seckill.controller;

import cn.hutool.core.util.ObjectUtil;
import com.duyi.seckill.domain.SeckillItem;
import com.duyi.seckill.domain.SeckillOrder;
import com.duyi.seckill.domain.User;
import com.duyi.seckill.dto.ResponseResult;
import com.duyi.seckill.dto.SeckillUrl;
import com.duyi.seckill.exception.UserException;
import com.duyi.seckill.service.SeckillItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillItemController {

    @Autowired
    private SeckillItemService seckillItemService;

    @RequestMapping("/seckillItemList")
    public ModelAndView showItem(){

        List<SeckillItem> list = seckillItemService.getAllItem();

        ModelAndView mv = new ModelAndView();
        mv.addObject("list",list);
        mv.setViewName("seckill_item");
        return mv;
    }

    @RequestMapping(value = "/intoSeckill/{seckillId}",method = RequestMethod.GET)
    public String intoSeckill(@PathVariable("seckillId") Integer id, Model model){

        SeckillItem item = seckillItemService.getItemById(id);
        model.addAttribute("item",item);

        return "detail";
    }

    //响应日期返回json数据
    @RequestMapping(value = "/now",method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<Long> now(){

        Date now = new Date();
        return new ResponseResult<>(true,now.getTime(),"now long");

    }

    /**
     * 获取商品的url
     */
    @RequestMapping(value = "/getSeckillUrl/{seckillId}",method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<SeckillUrl> getSeckillUrl(@PathVariable("seckillId") Integer id, HttpSession session){

        User user = (User) session.getAttribute("user");

        //判断用户是否登录
        if (ObjectUtil.isEmpty(user)){
            //没有登录
            throw new UserException("没有登录");
        }

        ResponseResult<SeckillUrl> result = new ResponseResult<>();

        try{
            SeckillUrl seckillUrl = seckillItemService.getSeckillUrlById(id);
            result.setData(seckillUrl);
            result.setSuccess(true);
            result.setMessage("ok");
            return result;
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    @RequestMapping(value = "/execute/{seckillId}/{md5}",method = RequestMethod.GET)
    public String execute(@PathVariable("seckillId") Integer id,
                          @PathVariable("md5") String md5,HttpSession session,
                          Model model){

//        ResponseResult<SeckillUrl> result = new ResponseResult<>();

        //1.验证请求的url是否正确
        boolean access = seckillItemService.verifyMD5(id,md5);
        if (!access){
//            result.setSuccess(false);
//            result.setMessage("md5 verify fail");
//            return result;
        }
        //2.判断用户是否登录

        User user = (User) session.getAttribute("user");
        if (ObjectUtil.isEmpty(user)){
//            result.setSuccess(false);
//            result.setMessage("user not exist");
//            return result;
        }
        //3.限制每一个用户只发送一次请求，第二次请求（5分钟内）不作处理


        //减库存 redis操作
        boolean success = seckillItemService.executeSeckill(user, id);
        //下订单(如果不成功)
        if (!success){
//            result.setSuccess(false);
//            result.setMessage("order fail");
//            return result;
        }
        //下订单（成功）mysql操作
        SeckillOrder seckillOrder = seckillItemService.placeOrder(user, id);


        return "redirect:/seckill/orderPay?orderCode=" + seckillOrder.getOrderCode();
    }

    @RequestMapping("/orderPay")
    public String orderPay(String orderCode, Model model){

        SeckillOrder seckillOrder = seckillItemService.getSeckillOrder(orderCode);

        model.addAttribute("seckillOrder" , seckillOrder);
        model.addAttribute("item" , seckillItemService.getItemById(seckillOrder.getSeckillItemId()));
        model.addAttribute("orderTime" , seckillOrder.getCreateTime());

        return "order";
    }


    //测试
    @RequestMapping(value = "/pay" , method = RequestMethod.GET)
    @ResponseBody
    public String pay(String orderCode){

        //超时订单不能支付
        SeckillOrder seckillOrder = seckillItemService.getSeckillOrder(orderCode);

        if (seckillOrder.getState() == 4){
            return "order expire";
        }


        seckillItemService.pay(orderCode);

        return "pay ok";
    }
}
