package com.secondhand.system.domain;

import com.secondhand.common.annotation.Excel;
import com.secondhand.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体
 */
public class Product extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long productId;

    /** 卖家ID */
    private Long sellerId;

    /** 卖家名称（关联查询，不存数据库） */
    private String sellerName;

    /** 类别ID */
    private Long categoryId;

    /** 类别名称（关联查询，不存数据库） */
    private String categoryName;

    /** 标题 */
    @Excel(name = "商品标题")
    private String title;

    /** 描述 */
    private String description;

    /** 品牌 */
    private String brand;

    /** 尺码 */
    private String size;

    /** 成色 */
    private String conditionLevel;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 售价 */
    private BigDecimal salePrice;

    /** 捐赠金额 */
    private BigDecimal donationAmount;

    /** 捐赠比例 */
    private BigDecimal donationRatio;

    /** 图片 */
    private String images;

    /** AI检测状态 */
    private String aiCheckStatus;

    /** AI检测结果 */
    private String aiCheckResult;

    /** AI检测时间 */
    private Date aiCheckTime;

    /** 状态 */
    private String status;

    /** 审核备注 */
    private String auditRemark;

    /** 审核时间 */
    private Date auditTime;

    /** 审核人 */
    private String auditBy;

    /** 浏览次数 */
    private Integer viewCount;

    /** 收藏次数 */
    private Integer favoriteCount;

    /** 更新时间 */
    private Date updateTime;

    /** 最小价格（查询条件，不存数据库） */
    private BigDecimal minPrice;

    /** 最大价格（查询条件，不存数据库） */
    private BigDecimal maxPrice;

    /** 排序字段（查询条件，不存数据库） */
    private String orderBy;

    /** 排序方式（查询条件，不存数据库） */
    private String order;

    /** 分类ID数组（查询条件，用于查询分类及其子分类，不存数据库） */
    private Long[] categoryIds;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getConditionLevel() { return conditionLevel; }
    public void setConditionLevel(String conditionLevel) { this.conditionLevel = conditionLevel; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public BigDecimal getDonationAmount() { return donationAmount; }
    public void setDonationAmount(BigDecimal donationAmount) { this.donationAmount = donationAmount; }
    public BigDecimal getDonationRatio() { return donationRatio; }
    public void setDonationRatio(BigDecimal donationRatio) { this.donationRatio = donationRatio; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getAiCheckStatus() { return aiCheckStatus; }
    public void setAiCheckStatus(String aiCheckStatus) { this.aiCheckStatus = aiCheckStatus; }
    public String getAiCheckResult() { return aiCheckResult; }
    public void setAiCheckResult(String aiCheckResult) { this.aiCheckResult = aiCheckResult; }
    public Date getAiCheckTime() { return aiCheckTime; }
    public void setAiCheckTime(Date aiCheckTime) { this.aiCheckTime = aiCheckTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
    @Override
    public Date getUpdateTime() { return updateTime; }
    @Override
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    public String getOrderBy() { return orderBy; }
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
    public Long[] getCategoryIds() { return categoryIds; }
    public void setCategoryIds(Long[] categoryIds) { this.categoryIds = categoryIds; }
}

