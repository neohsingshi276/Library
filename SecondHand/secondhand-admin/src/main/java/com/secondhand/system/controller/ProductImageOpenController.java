package com.secondhand.system.controller;

import com.secondhand.common.annotation.Log;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.system.domain.LLMImageRequest;
import com.secondhand.system.domain.LLMImageResponse;
import com.secondhand.system.service.ILLMImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 开放的商品图片识别接口（避免与系统 LLMImageController 重名冲突）
 */
@RestController
@RequestMapping("/llm/open/image")
public class ProductImageOpenController extends BaseController
{
    @Autowired
    private ILLMImageService llmImageService;

    /**
     * 通用衣物识别（JSON 提交 imageUrls 或 imageBase64s）
     */
    @PostMapping("/clothing/detect")
    public AjaxResult detectClothing(@RequestBody LLMImageRequest request)
    {
        request.setUserId(getUserId());
        request.setRequestType("clothing_detect_classify");
        LLMImageResponse response = llmImageService.detectAndClassifyClothing(request);
        return AjaxResult.success("识别完成", response);
    }

    /**
     * 上传文件识别（multipart 提交 file，可附加 prompt）
     */
    @Log(title = "LLM 图像识别上传", businessType = BusinessType.OTHER)
    @PostMapping("/upload/detect")
    public AjaxResult uploadAndDetect(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "prompt", required = false) String prompt)
    {
        try
        {
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            LLMImageRequest request = new LLMImageRequest();
            request.setUserId(getUserId());
            request.setPrompt(prompt);
            request.setRequestType("clothing_detect_classify");
            List<String> imgs = new ArrayList<>();
            imgs.add(base64);
            request.setImageBase64s(imgs);
            LLMImageResponse response = llmImageService.detectAndClassifyClothing(request);
            return AjaxResult.success("识别完成", response);
        }
        catch (Exception e)
        {
            return AjaxResult.error("上传识别失败：" + e.getMessage());
        }
    }
}


