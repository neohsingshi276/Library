package com.secondhand.system.mapper;

import com.secondhand.system.domain.ProductOrderReview;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单评价Mapper接口
 */
public interface ProductOrderReviewMapper
{
    /**
     * 查询评价
     */
    ProductOrderReview selectProductOrderReviewById(Long reviewId);

    /**
     * 根据订单ID和评价人信息查询评价
     */
    ProductOrderReview selectProductOrderReviewByOrderAndReviewer(@Param("orderId") Long orderId, @Param("reviewerId") Long reviewerId, @Param("reviewerType") String reviewerType);

    /**
     * 查询订单的所有评价
     */
    List<ProductOrderReview> selectProductOrderReviewListByOrderId(Long orderId);

    /**
     * 查询商品的所有评价
     */
    List<Map<String, Object>> selectProductOrderReviewListByProductId(Long productId);

    /**
     * 查询用户的评价列表（作为评价人）
     */
    List<Map<String, Object>> selectProductOrderReviewListByReviewerId(Long reviewerId);

    /**
     * 查询用户的被评价列表（作为被评价人）
     */
    List<Map<String, Object>> selectProductOrderReviewListByReviewedId(Long reviewedId);

    /**
     * 查询评价列表
     */
    List<ProductOrderReview> selectProductOrderReviewList(ProductOrderReview productOrderReview);

    /**
     * 新增评价
     */
    int insertProductOrderReview(ProductOrderReview productOrderReview);

    /**
     * 修改评价
     */
    int updateProductOrderReview(ProductOrderReview productOrderReview);

    /**
     * 删除评价
     */
    int deleteProductOrderReviewById(Long reviewId);

    /**
     * 批量删除评价
     */
    int deleteProductOrderReviewByIds(Long[] reviewIds);
}

