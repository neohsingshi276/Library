package com.secondhand.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * LLM智能助手配置类
 * 
 * 这个类负责管理AI智能助手的各种配置参数
 * 包括：API密钥、模型选择、请求参数等
 * 
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "llm")
public class LLMConfig
{
    /** AI服务的访问密钥 */
    private String apiKey;
    
    /** AI服务的接口地址（文本生成） */
    private String apiBaseUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    
    /** 多模态API的接口地址（图像分析） */
    private String multimodalApiUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    
    /** AI服务类型 */
    private String serviceType = "qwen";
    
    /** 默认使用的AI模型（文本对话） */
    private String defaultModel = "qwen-turbo";
    
    /** 多模态视觉模型（图片分析） */
    private String visionModel = "qwen-vl-plus";
    
    /** 每次对话的最大字数限制 */
    private Integer maxTokens = 2000;
    
    /** AI回答的创造性程度 */
    private Double temperature = 0.7;
    
    /** 请求超时时间 */
    private Integer timeout = 60;
    
    /** 是否支持图像分析功能 */
    private Boolean enableImageAnalysis = true;
    
    /** 单次请求最多上传的图片数量 */
    private Integer maxImages = 5;
    
    /** 单张图片的最大文件大小 */
    private Integer maxImageSize = 10;
    
    /** 支持的图片格式 */
    private String supportedImageFormats = "jpg,jpeg,png,gif,bmp";

    public String getApiKey() { 
        return apiKey; 
    }
    
    public void setApiKey(String apiKey) { 
        this.apiKey = apiKey; 
    }
    
    public String getApiBaseUrl() { 
        return apiBaseUrl; 
    }
    
    public void setApiBaseUrl(String apiBaseUrl) { 
        this.apiBaseUrl = apiBaseUrl; 
    }
    
    public String getMultimodalApiUrl() { 
        return multimodalApiUrl; 
    }
    
    public void setMultimodalApiUrl(String multimodalApiUrl) { 
        this.multimodalApiUrl = multimodalApiUrl; 
    }
    
    public String getServiceType() { 
        return serviceType; 
    }
    
    public void setServiceType(String serviceType) { 
        this.serviceType = serviceType; 
    }
    
    public String getDefaultModel() { 
        return defaultModel; 
    }
    
    public void setDefaultModel(String defaultModel) { 
        this.defaultModel = defaultModel; 
    }
    
    public String getVisionModel() { 
        return visionModel; 
    }
    
    public void setVisionModel(String visionModel) { 
        this.visionModel = visionModel; 
    }
    
    public Integer getMaxTokens() { 
        return maxTokens; 
    }
    
    public void setMaxTokens(Integer maxTokens) { 
        this.maxTokens = maxTokens; 
    }
    
    public Double getTemperature() { 
        return temperature; 
    }
    
    public void setTemperature(Double temperature) { 
        this.temperature = temperature; 
    }
    
    public Integer getTimeout() { 
        return timeout; 
    }
    
    public void setTimeout(Integer timeout) { 
        this.timeout = timeout; 
    }
    
    public Boolean getEnableImageAnalysis() { 
        return enableImageAnalysis; 
    }
    
    public void setEnableImageAnalysis(Boolean enableImageAnalysis) { 
        this.enableImageAnalysis = enableImageAnalysis; 
    }
    
    public Integer getMaxImages() { 
        return maxImages; 
    }
    
    public void setMaxImages(Integer maxImages) { 
        this.maxImages = maxImages; 
    }
    
    public Integer getMaxImageSize() { 
        return maxImageSize; 
    }
    
    public void setMaxImageSize(Integer maxImageSize) { 
        this.maxImageSize = maxImageSize; 
    }
    
    public String getSupportedImageFormats() { 
        return supportedImageFormats; 
    }
    
    public void setSupportedImageFormats(String supportedImageFormats) { 
        this.supportedImageFormats = supportedImageFormats; 
    }
}

