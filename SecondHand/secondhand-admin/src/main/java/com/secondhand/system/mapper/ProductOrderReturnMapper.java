package com.secondhand.system.mapper;

import com.secondhand.system.domain.ProductOrderReturn;

import java.util.List;
import java.util.Map;

/**
 * 订单退款/退货Mapper接口
 */
public interface ProductOrderReturnMapper
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
     * 根据用户ID查询退款/退货列表（通过订单关联，包含订单商品信息）- 买家视角
     */
    List<Map<String, Object>> selectProductOrderReturnListByUserId(Long userId);

    /**
     * 根据卖家ID查询退款/退货列表（通过订单关联，包含订单商品信息）- 卖家视角
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
}


