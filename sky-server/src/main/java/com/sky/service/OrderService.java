package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    OrderVO detailFind(Long id);

    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    void repeteOrder(Long id);

    OrderStatisticsVO checkStatistics();

    void transportOrder(Long id);

    void confirmOrder(Long id);

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    void completeOrder(Long id);
}
