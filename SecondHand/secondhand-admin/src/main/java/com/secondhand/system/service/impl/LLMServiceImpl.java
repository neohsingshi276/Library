package com.secondhand.system.service.impl;

import com.secondhand.system.config.LLMConfig;
import com.secondhand.system.domain.LLMRequest;
import com.secondhand.system.domain.LLMResponse;
import com.secondhand.system.service.ILLMService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.UUID;

/**
 * LLM智能助手服务实现类
 * 
 * @author ruoyi
 */
@Service
public class LLMServiceImpl implements ILLMService
{
    private static final Logger log = LoggerFactory.getLogger(LLMServiceImpl.class);

    @Autowired
    private LLMConfig llmConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public LLMResponse chatWithLLM(LLMRequest request)
    {
        String systemPrompt = "你是一位专业的AI助手，可以帮助用户解答各种问题。" +
                "请以专业、友好、准确的方式回答用户的问题。";

        return processLLMRequest(request, systemPrompt, "chat");
    }

    /**
     * 处理LLM请求的核心方法
     */
    private LLMResponse processLLMRequest(LLMRequest request, String systemPrompt, String requestType)
    {
        LLMResponse response = new LLMResponse();
        response.setResponseId(UUID.randomUUID().toString());
        response.setUserId(request.getUserId());
        response.setUserInput(request.getPrompt());
        response.setRequestType(requestType);
        response.setCreateTime(new Date());

        long startTime = System.currentTimeMillis();

        try
        {
            String fullPrompt = systemPrompt + "\n\n用户问题：" + request.getPrompt();

            ObjectNode requestBody;
            if ("qwen".equals(llmConfig.getServiceType())) {
                requestBody = objectMapper.createObjectNode();
                ObjectNode input = objectMapper.createObjectNode();
                ArrayNode messages = objectMapper.createArrayNode();
                ObjectNode message = objectMapper.createObjectNode();
                message.put("role", "user");
                message.put("content", fullPrompt);
                messages.add(message);
                input.set("messages", messages);
                requestBody.set("input", input);
                
                ObjectNode parameters = objectMapper.createObjectNode();
                parameters.put("temperature", request.getTemperature() != null ? request.getTemperature() : llmConfig.getTemperature());
                parameters.put("max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : llmConfig.getMaxTokens());
                requestBody.set("parameters", parameters);
                
                requestBody.put("model", request.getModel() != null ? request.getModel() : llmConfig.getDefaultModel());
            } else {
                requestBody = objectMapper.createObjectNode();
                requestBody.put("model", request.getModel() != null ? request.getModel() : llmConfig.getDefaultModel());
                requestBody.put("prompt", fullPrompt);
                requestBody.put("max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : llmConfig.getMaxTokens());
                requestBody.put("temperature", request.getTemperature() != null ? request.getTemperature() : llmConfig.getTemperature());
            }

            URL url = new URL(llmConfig.getApiBaseUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + llmConfig.getApiKey());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(llmConfig.getTimeout() * 1000);
            connection.setReadTimeout(llmConfig.getTimeout() * 1000);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsString(requestBody).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            String responseBody;
            
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    responseBody = responseBuilder.toString();
                }
                
                ObjectNode responseJson = (ObjectNode) objectMapper.readTree(responseBody);
                String aiResponse;
                
                if ("qwen".equals(llmConfig.getServiceType())) {
                    if (responseJson.has("output") && responseJson.get("output").has("text")) {
                        aiResponse = responseJson.get("output").get("text").asText();
                    } else if (responseJson.has("choices") && responseJson.get("choices").isArray()) {
                        aiResponse = responseJson.get("choices").get(0).get("message").get("content").asText();
                    } else {
                        throw new RuntimeException("通义千问API响应格式错误");
                    }
                } else {
                    if (responseJson.has("choices") && responseJson.get("choices").isArray()) {
                        aiResponse = responseJson.get("choices").get(0).get("text").asText();
                    } else {
                        throw new RuntimeException("OpenAI API响应格式错误");
                    }
                }
                
                response.setAiResponse(aiResponse);
                response.setModel(request.getModel() != null ? request.getModel() : llmConfig.getDefaultModel());
                response.setSuccess(true);
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    throw new RuntimeException("LLM API请求失败: " + responseCode + ", 错误信息: " + errorResponse.toString());
                }
            }

            long endTime = System.currentTimeMillis();
            response.setResponseTime(endTime - startTime);

            log.info("✅ {}模型请求成功 | 用户ID: {} | 请求类型: {} | 响应时间: {}ms",
                    llmConfig.getServiceType().toUpperCase(), request.getUserId(), requestType, response.getResponseTime());
        }
        catch (Exception e)
        {
            log.error("❌ {}模型请求失败 | 用户ID: {} | 错误: {}", 
                    llmConfig.getServiceType().toUpperCase(), request.getUserId(), e.getMessage(), e);

            response.setSuccess(false);
            response.setErrorMessage("LLM服务暂时不可用，请稍后重试");
            response.setAiResponse("抱歉，AI助手暂时无法响应您的请求，请稍后重试。");

            long endTime = System.currentTimeMillis();
            response.setResponseTime(endTime - startTime);
        }

        return response;
    }
}

