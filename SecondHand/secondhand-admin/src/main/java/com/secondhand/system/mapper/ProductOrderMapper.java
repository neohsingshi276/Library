package com.secondhand.system.mapper;

import com.secondhand.system.domain.ProductOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品订单Mapper接口
 */
public interface ProductOrderMapper
{
    /**
     * 查询订单
     */
    ProductOrder selectProductOrderById(Long orderId);

    /**
     * 根据订单号查询订单
     */
    ProductOrder selectProductOrderByOrderNo(String orderNo);

    /**
     * 查询订单列表
     */
    List<ProductOrder> selectProductOrderList(ProductOrder productOrder);

    /**
     * 新增订单
     */
    int insertProductOrder(ProductOrder productOrder);

    /**
     * 修改订单
     */
    int updateProductOrder(ProductOrder productOrder);

    /**
     * 删除订单
     */
    int deleteProductOrderById(Long orderId);

    /**
     * 批量删除订单
     */
    int deleteProductOrderByIds(Long[] orderIds);

    /**
     * 统计订单总数
     */
    Long countAllOrders();

    /**
     * 统计今日新增订单
     */
    Long countTodayOrders();

    /**
     * 统计订单总金额
     */
    BigDecimal sumOrderAmount();

    /**
     * 获取最近30天的订单趋势
     */
    List<Map<String, Object>> getOrderTrendLast30Days();

    /**
     * 获取最近12个月的交易金额统计
     */
    List<Map<String, Object>> getMonthlyTransactionAmount();
}

