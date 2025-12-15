package com.secondhand.system.domain;

import com.secondhand.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 订单评价实体
 */
public class ProductOrderReview extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private Long reviewId;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 商品ID */
    private Long productId;

    /** 评价人ID（买家或卖家） */
    private Long reviewerId;

    /** 评价人类型（buyer买家,seller卖家） */
    private String reviewerType;

    /** 被评价人ID（买家或卖家） */
    private Long reviewedId;

    /** 被评价人类型（buyer买家,seller卖家） */
    private String reviewedType;

    /** 评分（1-5星） */
    private Integer rating;

    /** 评价内容 */
    private String content;

    /** 评价图片（多个用逗号分隔） */
    private String reviewImages;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    // Getters and Setters
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public String getReviewerType() { return reviewerType; }
    public void setReviewerType(String reviewerType) { this.reviewerType = reviewerType; }
    public Long getReviewedId() { return reviewedId; }
    public void setReviewedId(Long reviewedId) { this.reviewedId = reviewedId; }
    public String getReviewedType() { return reviewedType; }
    public void setReviewedType(String reviewedType) { this.reviewedType = reviewedType; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getReviewImages() { return reviewImages; }
    public void setReviewImages(String reviewImages) { this.reviewImages = reviewImages; }
    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    @Override
    public Date getUpdateTime() { return updateTime; }
    @Override
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}








