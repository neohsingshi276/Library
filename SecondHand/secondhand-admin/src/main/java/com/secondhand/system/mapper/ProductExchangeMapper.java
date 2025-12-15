package com.secondhand.system.mapper;

import com.secondhand.system.domain.ProductExchange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品交换表Mapper
 */
public interface ProductExchangeMapper
{
    /**
     * 根据ID查询交换记录
     */
    ProductExchange selectProductExchangeById(Long exchangeId);

    /**
     * 根据交换单号查询交换记录
     */
    ProductExchange selectProductExchangeByNo(String exchangeNo);

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
     * 检查是否存在重复的交换请求
     */
    int checkDuplicateExchange(@Param("requesterId") Long requesterId, 
                                @Param("receiverId") Long receiverId, 
                                @Param("requesterProductId") Long requesterProductId, 
                                @Param("receiverProductId") Long receiverProductId);

    /**
     * 新增交换记录
     */
    int insertProductExchange(ProductExchange productExchange);

    /**
     * 修改交换记录
     */
    int updateProductExchange(ProductExchange productExchange);

    /**
     * 删除交换记录
     */
    int deleteProductExchangeById(Long exchangeId);

    /**
     * 批量删除交换记录
     */
    int deleteProductExchangeByIds(Long[] exchangeIds);

    /**
     * 统计交换总数
     */
    Long countAllExchanges();
}

