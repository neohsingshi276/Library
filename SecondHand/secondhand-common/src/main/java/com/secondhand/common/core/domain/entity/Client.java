package com.secondhand.common.core.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.secondhand.common.annotation.Excel;
import com.secondhand.common.core.domain.BaseEntity;

/**
 * 用户信息对象 client
 *
 * @author ruoyi
 * @date 2025-01-27
 */
public class Client extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户账号 */
    @Excel(name = "用户账号")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    /** 用户邮箱 */
    @Excel(name = "用户邮箱")
    private String email;

    /** 手机号码 */
    @Excel(name = "手机号码")
    private String phonenumber;

    /** 用户性别（0男 1女 2未知） */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /** 头像地址 */
    @Excel(name = "头像地址")
    private String avatar;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 帐号状态（0正常 1停用） */
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 信用积分（默认100分） */
    @Excel(name = "信用积分")
    private Integer creditScore;

    /** 累计捐赠金额 */
    @Excel(name = "累计捐赠金额")
    private java.math.BigDecimal totalDonation;

    /** 累计交易金额 */
    @Excel(name = "累计交易金额")
    private java.math.BigDecimal totalAmount;

    /** 累计订单数 */
    @Excel(name = "累计订单数")
    private Integer totalOrders;

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getNickName()
    {
        return nickName;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmail()
    {
        return email;
    }
    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }
    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getSex()
    {
        return sex;
    }
    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setCreditScore(Integer creditScore)
    {
        this.creditScore = creditScore;
    }

    public Integer getCreditScore()
    {
        return creditScore;
    }

    public void setTotalDonation(java.math.BigDecimal totalDonation)
    {
        this.totalDonation = totalDonation;
    }

    public java.math.BigDecimal getTotalDonation()
    {
        return totalDonation;
    }

    public void setTotalAmount(java.math.BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public java.math.BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalOrders(Integer totalOrders)
    {
        this.totalOrders = totalOrders;
    }

    public Integer getTotalOrders()
    {
        return totalOrders;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("nickName", getNickName())
                .append("email", getEmail())
                .append("phonenumber", getPhonenumber())
                .append("sex", getSex())
                .append("avatar", getAvatar())
                .append("password", getPassword())
                .append("status", getStatus())
                .append("creditScore", getCreditScore())
                .append("totalDonation", getTotalDonation())
                .append("totalAmount", getTotalAmount())
                .append("totalOrders", getTotalOrders())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
