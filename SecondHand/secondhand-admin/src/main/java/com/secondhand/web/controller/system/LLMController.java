package com.secondhand.web.controller.system;

import com.secondhand.common.annotation.Log;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.system.domain.LLMRequest;
import com.secondhand.system.domain.LLMResponse;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.system.service.ILLMService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * LLM智能助手控制器
 *
 * @author ruoyi
 */
@Api(tags = "LLM智能助手")
@RestController
@RequestMapping("/llm")
public class LLMController extends BaseController
{
    @Autowired
    private ILLMService llmService;

    /**
     * 通用AI对话
     */
    @ApiOperation("通用AI对话")
    @PreAuthorize("@ss.hasPermi('llm:chat:query')")
    @Log(title = "LLM对话", businessType = BusinessType.OTHER)
    @PostMapping("/chat")
    public AjaxResult chatWithLLM(@RequestBody LLMRequest request)
    {
        try
        {
            request.setUserId(SecurityUtils.getUserId());
            request.setRequestType("chat");

            LLMResponse response = llmService.chatWithLLM(request);
            return AjaxResult.success("对话处理成功", response);
        }
        catch (Exception e)
        {
            logger.error("LLM对话处理失败", e);
            return AjaxResult.error("LLM对话处理失败：" + e.getMessage());
        }
    }

    /**
     * 客户端通用AI对话
     */
    @ApiOperation("客户端通用AI对话")
    @Log(title = "客户端LLM对话", businessType = BusinessType.OTHER)
    @PostMapping("/client/chat")
    public AjaxResult getClientChat(@RequestBody LLMRequest request)
    {
        try
        {
            request.setUserId(SecurityUtils.getUserId());
            request.setRequestType("chat");

            LLMResponse response = llmService.chatWithLLM(request);
            return AjaxResult.success("对话处理成功", response);
        }
        catch (Exception e)
        {
            logger.error("LLM对话处理失败", e);
            return AjaxResult.error("LLM对话处理失败：" + e.getMessage());
        }
    }
}

