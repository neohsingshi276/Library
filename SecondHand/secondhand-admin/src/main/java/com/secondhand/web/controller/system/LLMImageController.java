package com.secondhand.web.controller.system;

import com.secondhand.common.annotation.Log;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.system.domain.LLMImageRequest;
import com.secondhand.system.domain.LLMImageResponse;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.system.service.ILLMImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

/**
 * LLM图像控制器
 * 专注于衣服识别和分类
 *
 * @author ruoyi
 */
@Api(tags = "衣服识别和分类")
@RestController
@RequestMapping("/llm/image")
public class LLMImageController extends BaseController
{
    @Autowired
    private ILLMImageService llmImageService;

    /**
     * 衣服检测和分类
     */
    @ApiOperation("衣服检测和分类")
    @PreAuthorize("@ss.hasPermi('llm:image:clothing:query')")
    @Log(title = "衣服识别分类", businessType = BusinessType.OTHER)
    @PostMapping("/clothing/detect")
    public AjaxResult detectAndClassifyClothing(@RequestBody LLMImageRequest request)
    {
        try
        {
            request.setUserId(SecurityUtils.getUserId());
            request.setRequestType("clothing_detect_classify");

            LLMImageResponse response = llmImageService.detectAndClassifyClothing(request);
            return AjaxResult.success("衣服识别完成", response);
        }
        catch (Exception e)
        {
            logger.error("衣服识别失败", e);
            return AjaxResult.error("衣服识别失败：" + e.getMessage());
        }
    }

    /**
     * 上传图片进行衣服识别
     */
    @ApiOperation("上传图片进行衣服识别")
    @PreAuthorize("@ss.hasPermi('llm:image:upload:query')")
    @Log(title = "上传图片衣服识别", businessType = BusinessType.OTHER)
    @PostMapping("/upload/clothing")
    public AjaxResult uploadAndDetectClothing(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prompt", required = false, defaultValue = "") String prompt)
    {
        try
        {
            byte[] fileBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(fileBytes);

            LLMImageRequest request = new LLMImageRequest();
            request.setUserId(SecurityUtils.getUserId());
            request.setPrompt(prompt);
            request.setRequestType("clothing_detect_classify");

            List<String> imageBase64s = new ArrayList<>();
            imageBase64s.add(base64Image);
            request.setImageBase64s(imageBase64s);

            LLMImageResponse response = llmImageService.detectAndClassifyClothing(request);

            return AjaxResult.success("衣服识别完成", response);
        }
        catch (Exception e)
        {
            logger.error("上传图片衣服识别失败", e);
            return AjaxResult.error("上传图片衣服识别失败：" + e.getMessage());
        }
    }

    /**
     * 客户端衣服识别
     */
    @ApiOperation("客户端衣服识别")
    @Log(title = "客户端衣服识别", businessType = BusinessType.OTHER)
    @PostMapping("/client/clothing/detect")
    public AjaxResult getClientClothingDetect(@RequestBody LLMImageRequest request)
    {
        try
        {
            request.setUserId(SecurityUtils.getUserId());
            request.setRequestType("clothing_detect_classify");

            LLMImageResponse response = llmImageService.detectAndClassifyClothing(request);
            return AjaxResult.success("衣服识别完成", response);
        }
        catch (Exception e)
        {
            logger.error("衣服识别失败", e);
            return AjaxResult.error("衣服识别失败：" + e.getMessage());
        }
    }
}

