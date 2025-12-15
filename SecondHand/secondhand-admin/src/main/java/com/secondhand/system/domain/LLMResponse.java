package com.secondhand.system.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * LLM响应对象
 * 
 * @author ruoyi
 */
public class LLMResponse implements Serializable
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

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Long getResponseTime() { return responseTime; }
    public void setResponseTime(Long responseTime) { this.responseTime = responseTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}

