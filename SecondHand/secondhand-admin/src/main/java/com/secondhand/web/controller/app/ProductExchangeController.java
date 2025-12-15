package com.secondhand.web.controller.app;

import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.page.TableDataInfo;
import com.secondhand.system.domain.ProductExchange;
import com.secondhand.system.service.IProductExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端商品交换接口
 */
@RestController
@RequestMapping("/app/exchange")
public class ProductExchangeController extends BaseController
{
    @Autowired
    private IProductExchangeService productExchangeService;

    /**
     * 查询我发起的交换请求列表
     */
    @GetMapping("/my/requests")
    public TableDataInfo getMyExchangeRequests()
    {
        Long userId = getUserId();
        startPage();
        List<ProductExchange> list = productExchangeService.selectMyExchangeRequests(userId);
        return getDataTable(list);
    }

    /**
     * 查询我接收的交换请求列表
     */
    @GetMapping("/my/receives")
    public TableDataInfo getMyExchangeReceives()
    {
        Long userId = getUserId();
        startPage();
        List<ProductExchange> list = productExchangeService.selectMyExchangeReceives(userId);
        return getDataTable(list);
    }

    /**
     * 查询交换详情
     */
    @GetMapping("/{exchangeId}")
    public AjaxResult getExchangeInfo(@PathVariable Long exchangeId)
    {
        ProductExchange exchange = productExchangeService.selectProductExchangeById(exchangeId);
        if (exchange == null)
        {
            return error("交换记录不存在");
        }
        // 验证权限：只有发起者或接收者可以查看
        Long userId = getUserId();
        if (!exchange.getRequesterId().equals(userId) && !exchange.getReceiverId().equals(userId))
        {
            return error("无权限查看此交换记录");
        }
        return success(exchange);
    }

    /**
     * 发起交换请求
     */
    @PostMapping("/create")
    public AjaxResult createExchangeRequest(@RequestBody ProductExchange exchange)
    {
        Long userId = getUserId();
        Long receiverProductId = exchange.getReceiverProductId();
        Long requesterProductId = exchange.getRequesterProductId();

        if (receiverProductId == null || requesterProductId == null)
        {
            return error("请选择要交换的商品");
        }

        try
        {
            int result = productExchangeService.createExchangeRequest(userId, receiverProductId, requesterProductId);
            return toAjax(result);
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 接受交换请求
     */
    @PutMapping("/{exchangeId}/accept")
    public AjaxResult acceptExchange(@PathVariable Long exchangeId)
    {
        Long userId = getUserId();
        try
        {
            int result = productExchangeService.acceptExchange(exchangeId, userId);
            return toAjax(result);
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 拒绝交换请求
     */
    @PutMapping("/{exchangeId}/reject")
    public AjaxResult rejectExchange(@PathVariable Long exchangeId, @RequestBody(required = false) ProductExchange exchange)
    {
        Long userId = getUserId();
        String rejectReason = exchange != null ? exchange.getRejectReason() : null;
        try
        {
            int result = productExchangeService.rejectExchange(exchangeId, userId, rejectReason);
            return toAjax(result);
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 发起者确认完成
     */
    @PutMapping("/{exchangeId}/requester/complete")
    public AjaxResult requesterComplete(@PathVariable Long exchangeId)
    {
        Long userId = getUserId();
        try
        {
            int result = productExchangeService.requesterComplete(exchangeId, userId);
            return toAjax(result);
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 接收者确认完成
     */
    @PutMapping("/{exchangeId}/receiver/complete")
    public AjaxResult receiverComplete(@PathVariable Long exchangeId)
    {
        Long userId = getUserId();
        try
        {
            int result = productExchangeService.receiverComplete(exchangeId, userId);
            return toAjax(result);
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 取消交换
     */
    @PutMapping("/{exchangeId}/cancel")
    public AjaxResult cancelExchange(@PathVariable Long exchangeId, @RequestBody(required = false) ProductExchange exchange)
    {
        Long userId = getUserId();
        String cancelReason = exchange != null ? exchange.getCancelReason() : null;
        try
        {
            int result = productExchangeService.cancelExchange(exchangeId, userId, cancelReason);
            return toAjax(result);
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }
}






