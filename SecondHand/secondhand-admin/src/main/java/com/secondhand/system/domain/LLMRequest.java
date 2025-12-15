package com.secondhand.system.domain;

import java.io.Serializable;

/**
 * LLM请求对象
 * 
 * @author ruoyi
 */
public class LLMRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 用户输入的问题或描述 */
    private String prompt;
    
    /** 模型名称 */
    private String model;
    
    /** 最大token数 */
    private Integer maxTokens;
    
    /** 温度参数 */
    private Double temperature;
    
    /** 用户ID */
    private Long userId;
    
    /** 请求类型 */
    private String requestType;

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
}

