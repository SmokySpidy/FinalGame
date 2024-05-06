package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.OrderStatusConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.controller.user.OrderController;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<OrderVO> page=orderMapper.pageQuery(ordersPageQueryDTO);
        //下一条sql进行分页，自动加入limit关键字分页
        List<OrderVO> result = page.getResult();
        List<OrderVO> list=new ArrayList<>();
        for (OrderVO orderVO : result) {
            StringJoiner sj=new StringJoiner(",","(",")");
            List<OrderDetail> orderDetails=orderDetailMapper.getByOrderId(orderVO.getId());
            orderDetails.forEach(orderDetail -> {
                sj.add(orderDetail.getName());
            });
            orderVO.setOrderDetailList(orderDetails);
            orderVO.setOrderDishes(sj.toString());
            list.add(orderVO);
        }
        return new PageResult(page.getTotal(), list);
    }

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

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public OrderVO detailFind(Long id) {
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        OrdersDTO ordersDTO=new OrdersDTO();
        ordersDTO.setId(id);
        //ordersDTO.setUserId(BaseContext.getCurrentId());
        List<Orders> list = orderMapper.list(ordersDTO);
        Orders orders=list.get(0);
        OrderVO orderVO=new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders=new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(OrderStatusConstant.ORDER_CANCELED);
        orderMapper.update(orders);
    }

    @Override
    public void repeteOrder(Long id) {
/*        OrdersDTO ordersDTO=new OrdersDTO();
        ordersDTO.setId(id);
        List<Orders> list = orderMapper.list(ordersDTO);
        Orders orders=list.get(0);
        orderMapper.insert(orders);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        orderDetails.forEach(orderDetail -> orderDetail.setOrderId(orders.getId()));
        orderDetailMapper.insertBatch(orderDetails);*/
        //1.清空 购物车
        //2.查订单明细表
        //3.往shoppingcart里面插入数据，userid为标识
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.delete(shoppingCart);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        orderDetails.forEach(orderDetail -> {
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCartMapper.insert(shoppingCart);
        });

    }

    @Override
    public OrderStatisticsVO checkStatistics() {
        OrderStatisticsVO orderStatisticsVO=new OrderStatisticsVO();
        int count_to_accept=orderMapper.countByStatus(OrderStatusConstant.ORDER_TO_ACCEPT);
        int count_to_transport=orderMapper.countByStatus(OrderStatusConstant.ORDER_ACCEPTED);
        int on_transport=orderMapper.countByStatus(OrderStatusConstant.ORDER_ON_TRAINSPORT);
        orderStatisticsVO.setToBeConfirmed(count_to_accept);
        orderStatisticsVO.setConfirmed(count_to_transport);
        orderStatisticsVO.setDeliveryInProgress(on_transport);
        return orderStatisticsVO;
    }

    @Override
    public void transportOrder(Long id) {
        Orders orders=new Orders();
        orders.setId(id);
        orders.setStatus(OrderStatusConstant.ORDER_ON_TRAINSPORT);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void confirmOrder(Long id) {
        Orders orders=new Orders();
        orders.setId(id);
        orders.setStatus(OrderStatusConstant.ORDER_ACCEPTED);
        orderMapper.update(orders);
    }

    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        //1.取消订单status=
        //2.支付状态=退款
        //3.rejectionresaon
        Orders orders=new Orders();
        orders.setId(ordersRejectionDTO.getId());
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setPayStatus(Orders.REFUND);
        orders.setStatus(OrderStatusConstant.ORDER_CANCELED);
        orderMapper.update(orders);
    }

    @Override
    public void completeOrder(Long id) {
        Orders orders=new Orders();
        orders.setStatus(OrderStatusConstant.ORDER_COMPLETED);
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setId(id);
        orderMapper.update(orders);
    }

}
