package com.secondhand.system.service;

import com.secondhand.system.domain.LLMRequest;
import com.secondhand.system.domain.LLMResponse;

/**
 * LLM服务接口
 * 
 * @author ruoyi
 */
public interface ILLMService
{
    /**
     * 通用AI对话
     * 
     * @param request LLM请求对象
     * @return LLM响应对象
     */
    LLMResponse chatWithLLM(LLMRequest request);
}

