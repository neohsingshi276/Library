package com.secondhand.system.domain;

import com.secondhand.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单退货实体
 */
public class ProductOrderReturn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 退货ID */
    private Long returnId;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 退货单号 */
    private String returnNo;

    /** 退款类型（refund仅退款,return_refund退货退款） */
    private String returnType;

    /** 退货原因 */
    private String returnReason;

    /** 退货说明 */
    private String returnDescription;

    /** 退货图片（多个用逗号分隔） */
    private String returnImages;

    /** 退货状态（requested申请中,approved已同意,rejected已拒绝,shipped已发货,completed已完成,cancelled已取消,timeout_approved超时自动同意） */
    private String returnStatus;

    /** 买家申请时的收货状态（received已收货 / not_received未收货） */
    private String receiveStatus;

    /** 退款金额 */
    private BigDecimal returnAmount;

    /** 退货运费（自费） */
    private BigDecimal returnShippingFee;

    /** 退货快递公司 */
    private String returnExpressCompany;

    /** 退货快递单号 */
    private String returnExpressNo;

    /** 退货发货时间 */
    private Date returnShippingTime;

    /** 卖家确认收货时间 */
    private Date sellerReceiveTime;

    /** 卖家收货备注 */
    private String sellerReceiveRemark;

    /** 退款完成时间 */
    private Date refundTime;

    /** 实际退款金额 */
    private BigDecimal refundAmount;

    /** 退款备注 */
    private String refundRemark;

    /** 同意时间 */
    private Date approveTime;

    /** 卖家处理截止时间（申请后48小时） */
    private Date approveDeadline;

    /** 拒绝原因 */
    private String rejectReason;

    /** 拒绝时间 */
    private Date rejectTime;

    /** 买家寄回截止时间（同意后7天） */
    private Date shippingDeadline;

    /** 完成时间 */
    private Date completeTime;

    /** 取消时间 */
    private Date cancelTime;

    // Getters and Setters
    public Long getReturnId() { return returnId; }
    public void setReturnId(Long returnId) { this.returnId = returnId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getReturnNo() { return returnNo; }
    public void setReturnNo(String returnNo) { this.returnNo = returnNo; }
    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }
    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }
    public String getReturnDescription() { return returnDescription; }
    public void setReturnDescription(String returnDescription) { this.returnDescription = returnDescription; }
    public String getReturnImages() { return returnImages; }
    public void setReturnImages(String returnImages) { this.returnImages = returnImages; }
    public String getReturnStatus() { return returnStatus; }
    public void setReturnStatus(String returnStatus) { this.returnStatus = returnStatus; }
    public String getReceiveStatus() { return receiveStatus; }
    public void setReceiveStatus(String receiveStatus) { this.receiveStatus = receiveStatus; }
    public BigDecimal getReturnAmount() { return returnAmount; }
    public void setReturnAmount(BigDecimal returnAmount) { this.returnAmount = returnAmount; }
    public BigDecimal getReturnShippingFee() { return returnShippingFee; }
    public void setReturnShippingFee(BigDecimal returnShippingFee) { this.returnShippingFee = returnShippingFee; }
    public String getReturnExpressCompany() { return returnExpressCompany; }
    public void setReturnExpressCompany(String returnExpressCompany) { this.returnExpressCompany = returnExpressCompany; }
    public String getReturnExpressNo() { return returnExpressNo; }
    public void setReturnExpressNo(String returnExpressNo) { this.returnExpressNo = returnExpressNo; }
    public Date getReturnShippingTime() { return returnShippingTime; }
    public void setReturnShippingTime(Date returnShippingTime) { this.returnShippingTime = returnShippingTime; }
    public Date getSellerReceiveTime() { return sellerReceiveTime; }
    public void setSellerReceiveTime(Date sellerReceiveTime) { this.sellerReceiveTime = sellerReceiveTime; }
    public String getSellerReceiveRemark() { return sellerReceiveRemark; }
    public void setSellerReceiveRemark(String sellerReceiveRemark) { this.sellerReceiveRemark = sellerReceiveRemark; }
    public Date getRefundTime() { return refundTime; }
    public void setRefundTime(Date refundTime) { this.refundTime = refundTime; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public String getRefundRemark() { return refundRemark; }
    public void setRefundRemark(String refundRemark) { this.refundRemark = refundRemark; }
    public Date getApproveTime() { return approveTime; }
    public void setApproveTime(Date approveTime) { this.approveTime = approveTime; }
    public Date getApproveDeadline() { return approveDeadline; }
    public void setApproveDeadline(Date approveDeadline) { this.approveDeadline = approveDeadline; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public Date getRejectTime() { return rejectTime; }
    public void setRejectTime(Date rejectTime) { this.rejectTime = rejectTime; }
    public Date getShippingDeadline() { return shippingDeadline; }
    public void setShippingDeadline(Date shippingDeadline) { this.shippingDeadline = shippingDeadline; }
    public Date getCompleteTime() { return completeTime; }
    public void setCompleteTime(Date completeTime) { this.completeTime = completeTime; }
    public Date getCancelTime() { return cancelTime; }
    public void setCancelTime(Date cancelTime) { this.cancelTime = cancelTime; }
    @Override
    public Date getCreateTime() { return super.getCreateTime(); }
    @Override
    public void setCreateTime(Date createTime) { super.setCreateTime(createTime); }
    @Override
    public Date getUpdateTime() { return super.getUpdateTime(); }
    @Override
    public void setUpdateTime(Date updateTime) { super.setUpdateTime(updateTime); }
}

