package com.secondhand.system.domain;

import java.io.Serializable;

/**
 * AI发布商品请求载体
 */
public class ProductAiRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Product product;

    private LLMImageRequest aiRequest;

    /** 可显式指定用户ID，否则走当前登录人 */
    private Long userId;

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public LLMImageRequest getAiRequest() { return aiRequest; }
    public void setAiRequest(LLMImageRequest aiRequest) { this.aiRequest = aiRequest; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}


