package com.secondhand.system.service;

import com.secondhand.system.domain.ProductExchange;

import java.util.List;

/**
 * 商品交换Service接口
 */
public interface IProductExchangeService
{
    /**
     * 查询交换记录
     */
    ProductExchange selectProductExchangeById(Long exchangeId);

    /**
     * 查询交换记录列表
     */
    List<ProductExchange> selectProductExchangeList(ProductExchange productExchange);

    /**
     * 查询我发起的交换请求
     */
    List<ProductExchange> selectMyExchangeRequests(Long requesterId);

    /**
     * 查询我接收的交换请求
     */
    List<ProductExchange> selectMyExchangeReceives(Long receiverId);

    /**
     * 发起交换请求
     */
    int createExchangeRequest(Long requesterId, Long receiverProductId, Long requesterProductId);

    /**
     * 接受交换请求
     */
    int acceptExchange(Long exchangeId, Long receiverId);

    /**
     * 拒绝交换请求
     */
    int rejectExchange(Long exchangeId, Long receiverId, String rejectReason);

    /**
     * 发起者确认完成
     */
    int requesterComplete(Long exchangeId, Long requesterId);

    /**
     * 接收者确认完成
     */
    int receiverComplete(Long exchangeId, Long receiverId);

    /**
     * 取消交换
     */
    int cancelExchange(Long exchangeId, Long userId, String cancelReason);

    /**
     * 删除交换记录
     */
    int deleteProductExchangeById(Long exchangeId);
}






