package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;
    @Override
//    66.实现下单方法
    @Transactional
    public void submit(Orders orders) {
//        获取当前用户id，可以从session或者从BaseContext中获取
        Long userId = BaseContext.getCurrent();

//        查询当前用户购物车信息,需要注入购物车Service
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartslist = shoppingCartService.list(queryWrapper);
        if(shoppingCartslist==null){
            throw new CustomException("购物车为空 不能下单");
        }

//        查询用户数据
        User user=userService.getById(userId);

//        查询地址数据
        Long address = orders.getAddressBookId();
        addressBookService.getById(address);


//        向订单表Orders插入数据,this就是orderservice,除了页面提交的三个参数外，还需要自己配置其他属性才能保存
        long orderId = IdWorker.getId();//使用IdWorker获取订单号
        AtomicInteger amount=new AtomicInteger(0);

        List<OrderDetail>orderDetails=shoppingCartslist.stream().map((item)->{//计算总金额
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setAmount(item.getAmount());
            //amount.addAndGet(item.getNumber().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(orderId));
        orders.setAmount(new BigDecimal(amount.get()));//遍历购物车的价格属性后累加，和警情小工具一样，估计要用流处理
        this.save(orders);
//        向订单明细表OrderDatils处插入数据
        orderDetailService.saveBatch(orderDetails);
//        下单完成，清空购物车数据
    }
}
