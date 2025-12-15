package com.secondhand.system.service;

import com.secondhand.system.domain.LLMImageRequest;
import com.secondhand.system.domain.LLMImageResponse;

/**
 * LLM图像服务接口
 * 专注于衣服识别和分类
 * 
 * @author ruoyi
 */
public interface ILLMImageService
{
    /**
     * 检测并分类衣服
     * 
     * @param request LLM图像请求对象
     * @return LLM图像响应对象
     */
    LLMImageResponse detectAndClassifyClothing(LLMImageRequest request);
}

