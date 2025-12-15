package com.secondhand.system.domain;

import java.io.Serializable;
import java.util.List;

/**
 * LLM图像请求对象
 * 
 * @author ruoyi
 */
public class LLMImageRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 用户输入的问题或描述 */
    private String prompt;
    
    /** 图片URL列表 */
    private List<String> imageUrls;
    
    /** 图片Base64列表 */
    private List<String> imageBase64s;
    
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
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public List<String> getImageBase64s() { return imageBase64s; }
    public void setImageBase64s(List<String> imageBase64s) { this.imageBase64s = imageBase64s; }
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

