package com.secondhand.system.service;

import com.secondhand.system.domain.ProductOrder;

import java.util.List;

/**
 * 商品订单Service接口
 */
public interface IProductOrderService
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
     * 批量删除订单
     */
    int deleteProductOrderByIds(Long[] orderIds);

    /**
     * 删除订单信息
     */
    int deleteProductOrderById(Long orderId);

    /**
     * 创建订单
     */
    ProductOrder createOrder(Long productId, Long addressId, String remark);

    /**
     * 支付订单（模拟支付）
     */
    int payOrder(String orderNo, String payMethod);

    /**
     * 取消订单
     */
    int cancelOrder(String orderNo, String cancelReason);

    /**
     * 生成订单号
     */
    String generateOrderNo();

    /**
     * 确认收货
     */
    int confirmReceive(String orderNo);

    /**
     * 卖家发货
     */
    int shipOrder(String orderNo, String expressCompany, String expressNo);

    /**
     * 申请退货
     */
    int applyReturn(Long orderId, String returnReason, String returnDescription, String returnImages);

    /**
     * 填写退货物流信息
     */
    int fillReturnShipping(Long returnId, String expressCompany, String expressNo);
}

