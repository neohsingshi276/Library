package com.secondhand.system.service;

import com.secondhand.system.domain.ProductOrderReturn;

import java.util.List;
import java.util.Map;

/**
 * 订单退款/退货Service接口
 */
public interface IProductOrderReturnService
{
    /**
     * 查询退款/退货
     */
    ProductOrderReturn selectProductOrderReturnById(Long returnId);

    /**
     * 根据退款单号查询
     */
    ProductOrderReturn selectProductOrderReturnByReturnNo(String returnNo);

    /**
     * 根据订单ID查询退款/退货列表
     */
    List<ProductOrderReturn> selectProductOrderReturnListByOrderId(Long orderId);

    /**
     * 根据订单号查询退款/退货列表
     */
    List<ProductOrderReturn> selectProductOrderReturnListByOrderNo(String orderNo);

    /**
     * 查询退款/退货列表
     */
    List<ProductOrderReturn> selectProductOrderReturnList(ProductOrderReturn productOrderReturn);

    /**
     * 根据用户ID查询退款/退货列表（包含订单商品信息）- 买家视角
     */
    List<Map<String, Object>> selectProductOrderReturnListByUserId(Long userId);

    /**
     * 根据卖家ID查询退款/退货列表（包含订单商品信息）- 卖家视角
     */
    List<Map<String, Object>> selectProductOrderReturnListBySellerId(Long sellerId);

    /**
     * 新增退款/退货
     */
    int insertProductOrderReturn(ProductOrderReturn productOrderReturn);

    /**
     * 修改退款/退货
     */
    int updateProductOrderReturn(ProductOrderReturn productOrderReturn);

    /**
     * 删除退款/退货
     */
    int deleteProductOrderReturnById(Long returnId);

    /**
     * 批量删除退款/退货
     */
    int deleteProductOrderReturnByIds(Long[] returnIds);

    /**
     * 申请退款/退货
     * @param orderId 订单ID
     * @param returnType 退款类型（refund仅退款,return_refund退货退款）
     * @param returnReason 退款原因
     * @param returnDescription 退款说明
     * @param returnImages 退款图片
     * @param receiveStatus 收货状态（received 已收货 / not_received 未收货）
     * @return 退款/退货记录
     */
    ProductOrderReturn applyReturn(Long orderId, String returnType, String returnReason, String returnDescription, String returnImages, String receiveStatus);

    /**
     * 卖家同意退款/退货
     */
    int approveReturn(Long returnId);

    /**
     * 卖家拒绝退款/退货
     */
    int rejectReturn(Long returnId, String rejectReason);

    /**
     * 买家填写退货物流信息
     */
    int fillReturnShipping(Long returnId, String expressCompany, String expressNo);

    /**
     * 卖家确认收到退货
     */
    int confirmReturnReceive(Long returnId);

    /**
     * 取消退款/退货申请
     */
    int cancelReturn(Long returnId);

    /**
     * 生成退款单号
     */
    String generateReturnNo();

    /**
     * 处理超时自动同意退款（定时任务调用）
     */
    void processTimeoutReturns();

    /**
     * 卖家退款（手动触发退款，适用于仅退款流程中状态为approved的情况）
     */
    int refundReturn(Long returnId);
}


