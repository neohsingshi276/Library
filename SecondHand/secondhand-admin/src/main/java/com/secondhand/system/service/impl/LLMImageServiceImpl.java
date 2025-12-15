package com.secondhand.system.service.impl;

import com.secondhand.system.config.LLMConfig;
import com.secondhand.system.domain.LLMImageRequest;
import com.secondhand.system.domain.LLMImageResponse;
import com.secondhand.system.service.ILLMImageService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.UUID;

/**
 * LLM图像服务实现类
 * 专注于衣服识别和分类
 *
 * @author ruoyi
 */
@Service
public class LLMImageServiceImpl implements ILLMImageService
{
    private static final Logger log = LoggerFactory.getLogger(LLMImageServiceImpl.class);

    @Autowired
    private LLMConfig llmConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public LLMImageResponse detectAndClassifyClothing(LLMImageRequest request)
    {
        String systemPrompt = "You are a professional clothing recognition and classification expert. Please analyze the provided image and complete the following tasks:\n" +
                "1. First, determine whether the image contains clothing (garments, apparel items)\n" +
                "2. If it is clothing, please identify and classify:\n" +
                "   - Main category: Top, Bottom, Outerwear, Dress, Accessories, Shoes, etc.\n" +
                "   - Detailed classification: T-shirt, Shirt, Pants, Skirt, Jacket, Hat, etc.\n" +
                "   - Identify color, style, brand (if visible)\n" +
                "   - Determine size range (if visible on label)\n" +
                "   - Assess condition (Brand New, 90% New, 80% New, etc.)\n" +
                "3. Please return the result in JSON format as follows:\n" +
                "{\n" +
                "  \"isClothing\": true/false,\n" +
                "  \"category\": \"Main Category (in English)\",\n" +
                "  \"subCategory\": \"Detailed Classification (in English)\",\n" +
                "  \"color\": \"Color (in English)\",\n" +
                "  \"style\": \"Style (in English)\",\n" +
                "  \"brand\": \"Brand (in English, if visible)\",\n" +
                "  \"size\": \"Size (if visible)\",\n" +
                "  \"condition\": \"Condition (in English)\",\n" +
                "  \"description\": \"Detailed description in English\"\n" +
                "}\n" +
                "4. IMPORTANT: All text fields (category, subCategory, color, style, brand, condition, description) must be in English. Do not use Chinese characters.\n" +
                "5. If it is not clothing, please return: {\"isClothing\": false, \"description\": \"This is not a clothing item\"}";

        return processLLMImageRequest(request, systemPrompt, "clothing_detect_classify");
    }

    /**
     * 处理LLM图像请求的通用方法
     */
    private LLMImageResponse processLLMImageRequest(LLMImageRequest request, String systemPrompt, String requestType)
    {
        LLMImageResponse response = new LLMImageResponse();
        response.setResponseId(UUID.randomUUID().toString());
        response.setUserId(request.getUserId());
        response.setUserInput(request.getPrompt());
        response.setRequestType(requestType);
        response.setCreateTime(new Date());

        long startTime = System.currentTimeMillis();

        try
        {
            String fullPrompt = systemPrompt;
            if (request.getPrompt() != null && !request.getPrompt().trim().isEmpty()) {
                fullPrompt += "\n\n用户补充说明：" + request.getPrompt();
            }

            ObjectNode requestBody = buildImageRequestBody(request, fullPrompt);
            String apiUrl = llmConfig.getMultimodalApiUrl();
            log.info("使用多模态API地址: {}", apiUrl);
            
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            String apiKey = llmConfig.getApiKey();
            if (apiKey != null) {
                apiKey = apiKey.trim();
            }
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(llmConfig.getTimeout() * 1000);
            connection.setReadTimeout(llmConfig.getTimeout() * 2000);
            
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            byte[] requestBodyBytes = requestBodyStr.getBytes("UTF-8");
            
            connection.setChunkedStreamingMode(8192);
            connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));
            
            log.info("请求体大小: {} bytes ({} KB)", requestBodyBytes.length, requestBodyBytes.length / 1024);
            
            try (OutputStream os = connection.getOutputStream()) {
                int offset = 0;
                int chunkSize = 8192;
                while (offset < requestBodyBytes.length) {
                    int length = Math.min(chunkSize, requestBodyBytes.length - offset);
                    os.write(requestBodyBytes, offset, length);
                    os.flush();
                    offset += length;
                }
            }

            int responseCode = connection.getResponseCode();
            log.info("LLM多模态API响应码: {}", responseCode);
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
                String aiResponse = parseImageResponse(responseJson);
                parseClothingResult(response, aiResponse);
                
                response.setAiResponse(aiResponse);
                response.setModel(request.getModel() != null ? request.getModel() : llmConfig.getVisionModel());
                response.setSuccess(true);
            } else {
                String errorBody = "";
                try {
                    if (connection.getErrorStream() != null) {
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                            StringBuilder errorResponse = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                errorResponse.append(responseLine.trim());
                            }
                            errorBody = errorResponse.toString();
                        }
                    }
                } catch (Exception readError) {
                    log.warn("读取错误响应失败: {}", readError.getMessage());
                }
                log.error("LLM多模态API请求失败: 响应码={}, 错误信息={}", responseCode, errorBody);
                
                if (responseCode == 401) {
                    throw new RuntimeException("API认证失败(401): API Key无效或已过期。请检查配置文件中的 api-key 是否正确。");
                } else if (responseCode == 403) {
                    throw new RuntimeException("权限不足(403): 该API Key没有多模态服务权限。请在阿里云控制台开通多模态服务。");
                } else {
                    throw new RuntimeException("LLM API请求失败: " + responseCode + ", 错误信息: " + errorBody);
                }
            }

            long endTime = System.currentTimeMillis();
            response.setResponseTime(endTime - startTime);

            log.info("衣服识别请求处理成功，用户ID: {}, 请求类型: {}, 响应时间: {}ms",
                    request.getUserId(), requestType, response.getResponseTime());
        }
        catch (Exception e)
        {
            log.error("衣服识别请求处理失败，用户ID: {}, 错误信息: {}", request.getUserId(), e.getMessage(), e);

            response.setSuccess(false);
            response.setErrorMessage("Clothing recognition service is temporarily unavailable, please try again later");
            response.setAiResponse("Sorry, the AI clothing recognition service is temporarily unable to respond to your request, please try again later.");
            response.setIsClothing(false);

            long endTime = System.currentTimeMillis();
            response.setResponseTime(endTime - startTime);
        }

        return response;
    }

    /**
     * 构建图像请求体
     */
    private ObjectNode buildImageRequestBody(LLMImageRequest request, String fullPrompt)
    {
        ObjectNode requestBody = objectMapper.createObjectNode();
        
        boolean useCompatibleMode = llmConfig.getMultimodalApiUrl() != null && 
                                   llmConfig.getMultimodalApiUrl().contains("compatible-mode");
        
        if ("qwen".equals(llmConfig.getServiceType()) && !useCompatibleMode) {
            // 通义千问原生格式：使用 type="image"，图片URL直接放在image字段
            ObjectNode input = objectMapper.createObjectNode();
            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            
            ArrayNode content = objectMapper.createArrayNode();
            
            // 文本内容
            ObjectNode textContent = objectMapper.createObjectNode();
            textContent.put("type", "text");
            textContent.put("text", fullPrompt);
            content.add(textContent);
            
            // 图片URL（通义千问原生格式：type="image"，image字段直接放URL）
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                for (String imageUrl : request.getImageUrls()) {
                    ObjectNode imageContent = objectMapper.createObjectNode();
                    imageContent.put("type", "image");
                    imageContent.put("image", normalizeImageUrl(imageUrl));
                    content.add(imageContent);
                }
            }
            
            // 图片Base64（通义千问原生格式：type="image"，image字段放base64数据）
            if (request.getImageBase64s() != null && !request.getImageBase64s().isEmpty()) {
                for (String imageBase64 : request.getImageBase64s()) {
                    ObjectNode imageContent = objectMapper.createObjectNode();
                    imageContent.put("type", "image");
                    String url = imageBase64.startsWith("data:") ? imageBase64 : "data:image/jpeg;base64," + imageBase64;
                    imageContent.put("image", url);
                    content.add(imageContent);
                }
            }
            
            message.set("content", content);
            messages.add(message);
            input.set("messages", messages);
            requestBody.set("input", input);
            
            ObjectNode parameters = objectMapper.createObjectNode();
            parameters.put("temperature", request.getTemperature() != null ? request.getTemperature() : llmConfig.getTemperature());
            parameters.put("max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : llmConfig.getMaxTokens());
            requestBody.set("parameters", parameters);
            
            String visionModel = request.getModel() != null ? request.getModel() : llmConfig.getVisionModel();
            requestBody.put("model", visionModel);
        } else {
            String visionModel = request.getModel() != null ? request.getModel() : llmConfig.getVisionModel();
            requestBody.put("model", visionModel);
            
            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            
            ArrayNode content = objectMapper.createArrayNode();
            
            ObjectNode textContent = objectMapper.createObjectNode();
            textContent.put("type", "text");
            textContent.put("text", fullPrompt);
            content.add(textContent);
            
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                for (String imageUrl : request.getImageUrls()) {
                    ObjectNode imageContent = objectMapper.createObjectNode();
                    imageContent.put("type", "image_url");
                    ObjectNode imageUrlObj = objectMapper.createObjectNode();
                    imageUrlObj.put("url", normalizeImageUrl(imageUrl));
                    imageContent.set("image_url", imageUrlObj);
                    content.add(imageContent);
                }
            }
            
            if (request.getImageBase64s() != null && !request.getImageBase64s().isEmpty()) {
                for (String imageBase64 : request.getImageBase64s()) {
                    ObjectNode imageContent = objectMapper.createObjectNode();
                    imageContent.put("type", "image_url");
                    ObjectNode imageUrlObj = objectMapper.createObjectNode();
                    imageUrlObj.put("url", "data:image/jpeg;base64," + imageBase64);
                    imageContent.set("image_url", imageUrlObj);
                    content.add(imageContent);
                }
            }
            
            message.set("content", content);
            messages.add(message);
            requestBody.set("messages", messages);
            
            requestBody.put("temperature", request.getTemperature() != null ? request.getTemperature() : llmConfig.getTemperature());
            requestBody.put("max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : llmConfig.getMaxTokens());
        }
        
        return requestBody;
    }

    /**
     * 将包含中文或空格的图片地址规范化为 ASCII（% 编码），避免多模态接口校验失败
     */
    private String normalizeImageUrl(String imageUrl) {
        try {
            // 先尝试直接创建 URI（已是 ASCII 时最快）
            return URI.create(imageUrl).toASCIIString();
        } catch (Exception e) {
            try {
                // 对含中文/空格的文件名部分单独进行 UTF-8 编码，避免把路径分隔符一起编码掉
                int lastSlash = imageUrl.lastIndexOf('/');
                if (lastSlash == -1 || lastSlash == imageUrl.length() - 1) {
                    return URI.create(imageUrl.replace(" ", "%20")).toASCIIString();
                }
                String prefix = imageUrl.substring(0, lastSlash + 1);
                String fileName = imageUrl.substring(lastSlash + 1);
                // 仅编码文件名，保留路径结构
                String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8")
                        .replace("+", "%20"); // 保留空格为 %20
                String encodedUrl = prefix + encodedFileName;
                return URI.create(encodedUrl).toASCIIString();
            } catch (Exception ex) {
                log.warn("图片URL规范化失败，使用原始地址: {}", ex.getMessage());
                return imageUrl;
            }
        }
    }

    /**
     * 解析图像响应
     */
    private String parseImageResponse(ObjectNode responseJson)
    {
        if ("qwen".equals(llmConfig.getServiceType())) {
            // 通义千问响应格式：优先检查 output.text
            if (responseJson.has("output")) {
                com.fasterxml.jackson.databind.JsonNode outputNode = responseJson.get("output");
                if (outputNode.has("text")) {
                    return outputNode.get("text").asText();
                }
                // 检查 output.choices[0].message.content（content可能是数组或字符串）
                if (outputNode.has("choices") && outputNode.get("choices").isArray() && outputNode.get("choices").size() > 0) {
                    com.fasterxml.jackson.databind.JsonNode choice = outputNode.get("choices").get(0);
                    if (choice.has("message")) {
                        com.fasterxml.jackson.databind.JsonNode message = choice.get("message");
                        if (message.has("content")) {
                            com.fasterxml.jackson.databind.JsonNode contentNode = message.get("content");
                            // content可能是字符串或数组
                            if (contentNode.isTextual()) {
                                return contentNode.asText();
                            } else if (contentNode.isArray() && contentNode.size() > 0) {
                                // content是数组，提取第一个元素的text字段
                                com.fasterxml.jackson.databind.JsonNode firstContent = contentNode.get(0);
                                if (firstContent.has("text")) {
                                    return firstContent.get("text").asText();
                                }
                            }
                        }
                    }
                }
            }
            // 兼容顶层choices格式
            if (responseJson.has("choices") && responseJson.get("choices").isArray() && responseJson.get("choices").size() > 0) {
                com.fasterxml.jackson.databind.JsonNode choice = responseJson.get("choices").get(0);
                if (choice.has("message")) {
                    com.fasterxml.jackson.databind.JsonNode message = choice.get("message");
                    if (message.has("content")) {
                        com.fasterxml.jackson.databind.JsonNode contentNode = message.get("content");
                        if (contentNode.isTextual()) {
                            return contentNode.asText();
                        } else if (contentNode.isArray() && contentNode.size() > 0) {
                            com.fasterxml.jackson.databind.JsonNode firstContent = contentNode.get(0);
                            if (firstContent.has("text")) {
                                return firstContent.get("text").asText();
                            }
                        }
                    }
                }
            }
            throw new RuntimeException("通义千问API响应格式错误，无法找到text或content字段");
        } else {
            // OpenAI响应格式
            if (responseJson.has("choices") && responseJson.get("choices").isArray() && responseJson.get("choices").size() > 0) {
                com.fasterxml.jackson.databind.JsonNode choice = responseJson.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    com.fasterxml.jackson.databind.JsonNode contentNode = choice.get("message").get("content");
                    if (contentNode.isTextual()) {
                        return contentNode.asText();
                    } else if (contentNode.isArray() && contentNode.size() > 0) {
                        com.fasterxml.jackson.databind.JsonNode firstContent = contentNode.get(0);
                        if (firstContent.has("text")) {
                            return firstContent.get("text").asText();
                        }
                    }
                } else if (choice.has("text")) {
                    return choice.get("text").asText();
                }
            }
            throw new RuntimeException("OpenAI API响应格式错误，无法找到text或content字段");
        }
    }

    /**
     * 解析衣服识别结果
     */
    private void parseClothingResult(LLMImageResponse response, String aiResponse)
    {
            // 如果响应为空，使用默认值
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            log.warn("AI response is empty, using default: assuming it is clothing");
            response.setIsClothing(true);
            response.setClothingCategory("Other");
            response.setClothingDetails("");
            return;
        }
        
        try {
            String jsonStr = aiResponse;
            
            // 提取JSON代码块
            if (jsonStr.contains("```json")) {
                int start = jsonStr.indexOf("```json") + 7;
                int end = jsonStr.indexOf("```", start);
                if (end > start) {
                    jsonStr = jsonStr.substring(start, end).trim();
                }
            } else if (jsonStr.contains("```")) {
                int start = jsonStr.indexOf("```") + 3;
                int end = jsonStr.indexOf("```", start);
                if (end > start) {
                    jsonStr = jsonStr.substring(start, end).trim();
                }
            }
            
            // 提取JSON对象
            int jsonStart = jsonStr.indexOf("{");
            int jsonEnd = jsonStr.lastIndexOf("}");
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                jsonStr = jsonStr.substring(jsonStart, jsonEnd + 1);
            }
            
            // 安全地解析JSON
            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(jsonStr);
            if (!rootNode.isObject()) {
                throw new RuntimeException("JSON根节点不是对象类型");
            }
            
            ObjectNode jsonNode = (ObjectNode) rootNode;
            
            // 解析isClothing
            if (jsonNode.has("isClothing") && jsonNode.get("isClothing").isBoolean()) {
                response.setIsClothing(jsonNode.get("isClothing").asBoolean());
            }
            
            // 解析category
            if (jsonNode.has("category") && jsonNode.get("category").isTextual()) {
                response.setClothingCategory(jsonNode.get("category").asText());
            }
            
            response.setClothingDetails(jsonStr);
            
        } catch (Exception e) {
            log.warn("解析衣服识别结果失败，将使用文本匹配: {}", e.getMessage());
            // 使用文本匹配作为后备方案
            String lowerResponse = aiResponse.toLowerCase();
            boolean isClothing = false;
            String category = null;
            
            // 判断是否为衣服
            if (lowerResponse.contains("not clothing") || lowerResponse.contains("not a clothing") || 
                lowerResponse.contains("isclothing\":false") || lowerResponse.contains("\"isClothing\": false")) {
                isClothing = false;
            } else if (lowerResponse.contains("is clothing") || lowerResponse.contains("is a clothing") ||
                       lowerResponse.contains("isclothing\":true") || lowerResponse.contains("\"isClothing\": true") ||
                       lowerResponse.contains("clothing") || lowerResponse.contains("garment") || lowerResponse.contains("apparel")) {
                isClothing = true;
            }
            
            // 尝试提取类别信息（英文类别）
            if (isClothing) {
                String[] categories = {"Top", "Bottom", "Outerwear", "Dress", "Accessories", "Shoes", "T-shirt", "Shirt", "Pants", "Skirt", "Jacket", "Hat"};
                for (String cat : categories) {
                    if (lowerResponse.contains(cat.toLowerCase())) {
                        category = cat;
                        break;
                    }
                }
            }
            
            response.setIsClothing(isClothing);
            response.setClothingCategory(category);
            response.setClothingDetails(aiResponse);
        }
    }
}

