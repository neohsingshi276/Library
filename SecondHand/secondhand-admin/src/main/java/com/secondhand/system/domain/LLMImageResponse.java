package com.secondhand.system.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * LLM图像响应对象
 * 专注于衣服识别和分类
 * 
 * @author ruoyi
 */
public class LLMImageResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 响应ID */
    private String responseId;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户输入 */
    private String userInput;
    
    /** AI响应内容 */
    private String aiResponse;
    
    /** 请求类型 */
    private String requestType;
    
    /** 使用的模型 */
    private String model;
    
    /** 响应时间(毫秒) */
    private Long responseTime;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 是否成功 */
    private Boolean success;
    
    /** 错误信息 */
    private String errorMessage;
    
    /** 是否为衣服（true是衣服，false不是衣服） */
    private Boolean isClothing;
    
    /** 衣服分类（如：上衣、下装、外套、配饰等） */
    private String clothingCategory;
    
    /** 衣服详细信息（JSON格式，包含品牌、颜色、风格等） */
    private String clothingDetails;

    public String getResponseId()
    {
        return responseId;
    }
    
    public void setResponseId(String responseId)
    {
        this.responseId = responseId;
    }
    
    public Long getUserId()
    {
        return userId;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public String getUserInput()
    {
        return userInput;
    }
    
    public void setUserInput(String userInput)
    {
        this.userInput = userInput;
    }
    
    public String getAiResponse()
    {
        return aiResponse;
    }
    
    public void setAiResponse(String aiResponse)
    {
        this.aiResponse = aiResponse;
    }
    
    public String getRequestType()
    {
        return requestType;
    }
    
    public void setRequestType(String requestType)
    {
        this.requestType = requestType;
    }
    
    public String getModel()
    {
        return model;
    }
    
    public void setModel(String model)
    {
        this.model = model;
    }
    
    public Long getResponseTime()
    {
        return responseTime;
    }
    
    public void setResponseTime(Long responseTime)
    {
        this.responseTime = responseTime;
    }
    
    public Date getCreateTime()
    {
        return createTime;
    }
    
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
    
    public Boolean getSuccess()
    {
        return success;
    }
    
    public void setSuccess(Boolean success)
    {
        this.success = success;
    }
    
    public String getErrorMessage()
    {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
    
    public Boolean getIsClothing()
    {
        return isClothing;
    }
    
    public void setIsClothing(Boolean isClothing)
    {
        this.isClothing = isClothing;
    }
    
    public String getClothingCategory()
    {
        return clothingCategory;
    }
    
    public void setClothingCategory(String clothingCategory)
    {
        this.clothingCategory = clothingCategory;
    }
    
    public String getClothingDetails()
    {
        return clothingDetails;
    }
    
    public void setClothingDetails(String clothingDetails)
    {
        this.clothingDetails = clothingDetails;
    }
}

