package com.secondhand.system.domain;

import com.secondhand.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 商品交换实体
 */
public class ProductExchange extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交换ID */
    private Long exchangeId;

    /** 交换单号 */
    private String exchangeNo;

    /** 发起者ID（用户A） */
    private Long requesterId;

    /** 接收者ID（用户B） */
    private Long receiverId;

    /** 发起者的商品ID（A要换出的商品） */
    private Long requesterProductId;

    /** 接收者的商品ID（B的商品，A想要的） */
    private Long receiverProductId;

    /** 交换状态（pending待处理,accepted已接受,rejected已拒绝,completed已完成,cancelled已取消） */
    private String exchangeStatus;

    /** 是否已显示联系方式（0否 1是） */
    private String contactRevealed;

    /** 发起者是否完成（0否 1是） */
    private String requesterComplete;

    /** 接收者是否完成（0否 1是） */
    private String receiverComplete;

    /** 拒绝原因 */
    private String rejectReason;

    /** 拒绝时间 */
    private Date rejectTime;

    /** 接受时间 */
    private Date acceptTime;

    /** 联系方式显示时间 */
    private Date contactRevealTime;

    /** 完成时间 */
    private Date completeTime;

    /** 取消时间 */
    private Date cancelTime;

    /** 取消原因 */
    private String cancelReason;

    // 关联查询字段（不存数据库）
    /** 发起者信息 */
    private String requesterName;
    private String requesterAvatar;
    private String requesterPhone;
    private String requesterEmail;

    /** 接收者信息 */
    private String receiverName;
    private String receiverAvatar;
    private String receiverPhone;
    private String receiverEmail;

    /** 发起者商品信息 */
    private String requesterProductTitle;
    private String requesterProductImage;
    private String requesterProductStatus;

    /** 接收者商品信息 */
    private String receiverProductTitle;
    private String receiverProductImage;
    private String receiverProductStatus;

    // Getters and Setters
    public Long getExchangeId() { return exchangeId; }
    public void setExchangeId(Long exchangeId) { this.exchangeId = exchangeId; }

    public String getExchangeNo() { return exchangeNo; }
    public void setExchangeNo(String exchangeNo) { this.exchangeNo = exchangeNo; }

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public Long getRequesterProductId() { return requesterProductId; }
    public void setRequesterProductId(Long requesterProductId) { this.requesterProductId = requesterProductId; }

    public Long getReceiverProductId() { return receiverProductId; }
    public void setReceiverProductId(Long receiverProductId) { this.receiverProductId = receiverProductId; }

    public String getExchangeStatus() { return exchangeStatus; }
    public void setExchangeStatus(String exchangeStatus) { this.exchangeStatus = exchangeStatus; }

    public String getContactRevealed() { return contactRevealed; }
    public void setContactRevealed(String contactRevealed) { this.contactRevealed = contactRevealed; }

    public String getRequesterComplete() { return requesterComplete; }
    public void setRequesterComplete(String requesterComplete) { this.requesterComplete = requesterComplete; }

    public String getReceiverComplete() { return receiverComplete; }
    public void setReceiverComplete(String receiverComplete) { this.receiverComplete = receiverComplete; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public Date getRejectTime() { return rejectTime; }
    public void setRejectTime(Date rejectTime) { this.rejectTime = rejectTime; }

    public Date getAcceptTime() { return acceptTime; }
    public void setAcceptTime(Date acceptTime) { this.acceptTime = acceptTime; }

    public Date getContactRevealTime() { return contactRevealTime; }
    public void setContactRevealTime(Date contactRevealTime) { this.contactRevealTime = contactRevealTime; }

    public Date getCompleteTime() { return completeTime; }
    public void setCompleteTime(Date completeTime) { this.completeTime = completeTime; }

    public Date getCancelTime() { return cancelTime; }
    public void setCancelTime(Date cancelTime) { this.cancelTime = cancelTime; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    // 关联查询字段的getter和setter
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getRequesterAvatar() { return requesterAvatar; }
    public void setRequesterAvatar(String requesterAvatar) { this.requesterAvatar = requesterAvatar; }

    public String getRequesterPhone() { return requesterPhone; }
    public void setRequesterPhone(String requesterPhone) { this.requesterPhone = requesterPhone; }

    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverAvatar() { return receiverAvatar; }
    public void setReceiverAvatar(String receiverAvatar) { this.receiverAvatar = receiverAvatar; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }

    public String getRequesterProductTitle() { return requesterProductTitle; }
    public void setRequesterProductTitle(String requesterProductTitle) { this.requesterProductTitle = requesterProductTitle; }

    public String getRequesterProductImage() { return requesterProductImage; }
    public void setRequesterProductImage(String requesterProductImage) { this.requesterProductImage = requesterProductImage; }

    public String getRequesterProductStatus() { return requesterProductStatus; }
    public void setRequesterProductStatus(String requesterProductStatus) { this.requesterProductStatus = requesterProductStatus; }

    public String getReceiverProductTitle() { return receiverProductTitle; }
    public void setReceiverProductTitle(String receiverProductTitle) { this.receiverProductTitle = receiverProductTitle; }

    public String getReceiverProductImage() { return receiverProductImage; }
    public void setReceiverProductImage(String receiverProductImage) { this.receiverProductImage = receiverProductImage; }

    public String getReceiverProductStatus() { return receiverProductStatus; }
    public void setReceiverProductStatus(String receiverProductStatus) { this.receiverProductStatus = receiverProductStatus; }
}






