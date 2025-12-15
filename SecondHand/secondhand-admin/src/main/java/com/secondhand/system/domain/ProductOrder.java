package com.secondhand.system.domain;

import com.secondhand.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品订单实体
 */
public class ProductOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 买家ID */
    private Long userId;

    /** 商品ID */
    private Long productId;

    /** 卖家ID */
    private Long sellerId;

    /** 商品标题（冗余字段） */
    private String productTitle;

    /** 商品图片（冗余字段） */
    private String productImage;

    /** 售价 */
    private BigDecimal salePrice;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 订单金额 */
    private BigDecimal orderAmount;

    /** 订单状态 */
    private String orderStatus;

    /** 支付状态（0未支付 1已支付） */
    private String payStatus;

    /** 支付时间 */
    private Date payTime;

    /** 支付方式 */
    private String payMethod;

    /** 支付交易号 */
    private String payTransactionId;

    /** 发货状态（0未发货 1已发货） */
    private String shippingStatus;

    /** 发货时间 */
    private Date shippingTime;

    /** 快递公司 */
    private String expressCompany;

    /** 快递单号 */
    private String expressNo;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人电话 */
    private String receiverPhone;

    /** 收货省份 */
    private String receiverProvince;

    /** 收货城市 */
    private String receiverCity;

    /** 收货区/县 */
    private String receiverDistrict;

    /** 收货详细地址 */
    private String receiverDetailAddress;

    /** 收货邮政编码 */
    private String receiverPostalCode;

    /** 订单备注 */
    private String remark;

    /** 取消原因 */
    private String cancelReason;

    /** 取消时间 */
    private Date cancelTime;

    /** 完成时间 */
    private Date completeTime;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getOrderAmount() { return orderAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    public String getPayTransactionId() { return payTransactionId; }
    public void setPayTransactionId(String payTransactionId) { this.payTransactionId = payTransactionId; }
    public String getShippingStatus() { return shippingStatus; }
    public void setShippingStatus(String shippingStatus) { this.shippingStatus = shippingStatus; }
    public Date getShippingTime() { return shippingTime; }
    public void setShippingTime(Date shippingTime) { this.shippingTime = shippingTime; }
    public String getExpressCompany() { return expressCompany; }
    public void setExpressCompany(String expressCompany) { this.expressCompany = expressCompany; }
    public String getExpressNo() { return expressNo; }
    public void setExpressNo(String expressNo) { this.expressNo = expressNo; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getReceiverProvince() { return receiverProvince; }
    public void setReceiverProvince(String receiverProvince) { this.receiverProvince = receiverProvince; }
    public String getReceiverCity() { return receiverCity; }
    public void setReceiverCity(String receiverCity) { this.receiverCity = receiverCity; }
    public String getReceiverDistrict() { return receiverDistrict; }
    public void setReceiverDistrict(String receiverDistrict) { this.receiverDistrict = receiverDistrict; }
    public String getReceiverDetailAddress() { return receiverDetailAddress; }
    public void setReceiverDetailAddress(String receiverDetailAddress) { this.receiverDetailAddress = receiverDetailAddress; }
    public String getReceiverPostalCode() { return receiverPostalCode; }
    public void setReceiverPostalCode(String receiverPostalCode) { this.receiverPostalCode = receiverPostalCode; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public Date getCancelTime() { return cancelTime; }
    public void setCancelTime(Date cancelTime) { this.cancelTime = cancelTime; }
    public Date getCompleteTime() { return completeTime; }
    public void setCompleteTime(Date completeTime) { this.completeTime = completeTime; }
    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    @Override
    public Date getUpdateTime() { return updateTime; }
    @Override
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}

