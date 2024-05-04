package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1.根据地址id查询地址，判断是否存在
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null){
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //2.根据用户id判断用户购物车是否为空
        Long userId= BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart().builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list==null||list.size()==0){
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //3.创建订单实体，完善数据并插入
        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setUserId(userId);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderTime(LocalDateTime.now());


        //向订单表插入1条数据
        orderMapper.insert(orders);
        //4.根据订单id来往订单明细表插入多组数据
        List<OrderDetail> orderDetails=new ArrayList<>();
        list.forEach(shoppingCart0-> {
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(shoppingCart0,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);

        });
        orderDetailMapper.insertBatch(orderDetails);
        //5.根据用户id清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        //6.封装并返回vo数据
        OrderSubmitVO orderSubmitVO=new OrderSubmitVO().builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();

        return orderSubmitVO;
    }
}
