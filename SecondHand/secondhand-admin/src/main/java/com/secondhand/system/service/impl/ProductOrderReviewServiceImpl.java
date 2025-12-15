package com.secondhand.system.service.impl;

import com.secondhand.common.utils.DateUtils;
import com.secondhand.system.domain.ProductOrder;
import com.secondhand.system.domain.ProductOrderReview;
import com.secondhand.system.mapper.ProductOrderMapper;
import com.secondhand.system.mapper.ProductOrderReviewMapper;
import com.secondhand.system.service.IProductOrderReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单评价Service业务层处理
 */
@Service
public class ProductOrderReviewServiceImpl implements IProductOrderReviewService
{
    private static final Logger log = LoggerFactory.getLogger(ProductOrderReviewServiceImpl.class);

    @Autowired
    private ProductOrderReviewMapper productOrderReviewMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Override
    public ProductOrderReview selectProductOrderReviewById(Long reviewId)
    {
        return productOrderReviewMapper.selectProductOrderReviewById(reviewId);
    }

    @Override
    public ProductOrderReview selectProductOrderReviewByOrderAndReviewer(Long orderId, Long reviewerId, String reviewerType)
    {
        return productOrderReviewMapper.selectProductOrderReviewByOrderAndReviewer(orderId, reviewerId, reviewerType);
    }

    @Override
    public List<ProductOrderReview> selectProductOrderReviewListByOrderId(Long orderId)
    {
        return productOrderReviewMapper.selectProductOrderReviewListByOrderId(orderId);
    }

    @Override
    public List<Map<String, Object>> selectProductOrderReviewListByProductId(Long productId)
    {
        return productOrderReviewMapper.selectProductOrderReviewListByProductId(productId);
    }

    @Override
    public List<Map<String, Object>> selectProductOrderReviewListByReviewerId(Long reviewerId)
    {
        return productOrderReviewMapper.selectProductOrderReviewListByReviewerId(reviewerId);
    }

    @Override
    public List<Map<String, Object>> selectProductOrderReviewListByReviewedId(Long reviewedId)
    {
        return productOrderReviewMapper.selectProductOrderReviewListByReviewedId(reviewedId);
    }

    @Override
    public List<ProductOrderReview> selectProductOrderReviewList(ProductOrderReview productOrderReview)
    {
        return productOrderReviewMapper.selectProductOrderReviewList(productOrderReview);
    }

    @Override
    public int insertProductOrderReview(ProductOrderReview productOrderReview)
    {
        productOrderReview.setCreateTime(DateUtils.getNowDate());
        return productOrderReviewMapper.insertProductOrderReview(productOrderReview);
    }

    @Override
    public int updateProductOrderReview(ProductOrderReview productOrderReview)
    {
        productOrderReview.setUpdateTime(DateUtils.getNowDate());
        return productOrderReviewMapper.updateProductOrderReview(productOrderReview);
    }

    @Override
    public int deleteProductOrderReviewByIds(Long[] reviewIds)
    {
        return productOrderReviewMapper.deleteProductOrderReviewByIds(reviewIds);
    }

    @Override
    public int deleteProductOrderReviewById(Long reviewId)
    {
        return productOrderReviewMapper.deleteProductOrderReviewById(reviewId);
    }

    @Override
    @Transactional
    public ProductOrderReview createReview(Long userId, Long orderId, Integer rating, String content, String reviewImages)
    {
        // 查询订单信息
        ProductOrder order = productOrderMapper.selectProductOrderById(orderId);
        if (order == null)
        {
            throw new RuntimeException("订单不存在");
        }

        // 检查订单状态是否为已完成
        if (!"completed".equals(order.getOrderStatus()))
        {
            throw new RuntimeException("只有已完成的订单才能评价");
        }

        // 验证用户ID
        if (userId == null)
        {
            throw new RuntimeException("请先登录");
        }

        Long currentUserId = userId;

        // 判断当前用户是买家还是卖家
        String reviewerType;
        Long reviewedId;
        String reviewedType;

        if (currentUserId.equals(order.getUserId()))
        {
            // 当前用户是买家，评价卖家
            reviewerType = "buyer";
            reviewedId = order.getSellerId();
            reviewedType = "seller";
        }
        else if (currentUserId.equals(order.getSellerId()))
        {
            // 当前用户是卖家，评价买家
            reviewerType = "seller";
            reviewedId = order.getUserId();
            reviewedType = "buyer";
        }
        else
        {
            throw new RuntimeException("无权限评价此订单");
        }

        // 检查是否已经评价过
        ProductOrderReview existingReview = productOrderReviewMapper.selectProductOrderReviewByOrderAndReviewer(
            orderId, currentUserId, reviewerType);
        if (existingReview != null)
        {
            throw new RuntimeException("您已经评价过此订单，不能重复评价");
        }

        // 验证评分范围
        if (rating == null || rating < 1 || rating > 5)
        {
            throw new RuntimeException("评分必须在1-5星之间");
        }

        // 创建评价
        ProductOrderReview review = new ProductOrderReview();
        review.setOrderId(orderId);
        review.setOrderNo(order.getOrderNo());
        review.setProductId(order.getProductId());
        review.setReviewerId(currentUserId);
        review.setReviewerType(reviewerType);
        review.setReviewedId(reviewedId);
        review.setReviewedType(reviewedType);
        review.setRating(rating);
        review.setContent(content);
        review.setReviewImages(reviewImages);
        review.setCreateTime(DateUtils.getNowDate());
        review.setUpdateTime(DateUtils.getNowDate());

        int result = productOrderReviewMapper.insertProductOrderReview(review);
        if (result > 0)
        {
            return productOrderReviewMapper.selectProductOrderReviewById(review.getReviewId());
        }
        else
        {
            throw new RuntimeException("评价失败");
        }
    }

}

