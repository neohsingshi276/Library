package com.secondhand.web.controller.app;

import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.page.TableDataInfo;
import com.secondhand.system.domain.ProductOrder;
import com.secondhand.system.domain.ProductOrderReturn;
import com.secondhand.system.service.IProductOrderReturnService;
import com.secondhand.system.service.IProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/app/order")
public class OrderController extends BaseController
{
    @Autowired
    private IProductOrderService productOrderService;

    @Autowired
    private IProductOrderReturnService productOrderReturnService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public AjaxResult createOrder(@RequestBody Map<String, Object> params)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        if (!params.containsKey("productId") || !params.containsKey("addressId"))
        {
            return error("参数不完整");
        }
        
        Long productId = Long.valueOf(params.get("productId").toString());
        Long addressId = Long.valueOf(params.get("addressId").toString());
        String remark = params.containsKey("remark") && params.get("remark") != null 
            ? params.get("remark").toString() : null;
        
        try
        {
            ProductOrder order = productOrderService.createOrder(productId, addressId, remark);
            return success(order);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 查询订单列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ProductOrder order)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return getDataTable(null);
        }

        // 买家视角：前端未传 sellerId，则按 userId 过滤
        // 卖家视角：前端传 sellerId（商品发布者），不强制覆盖
        if (order.getSellerId() == null && order.getUserId() == null)
        {
            order.setUserId(userId);
        }
        startPage();
        List<ProductOrder> list = productOrderService.selectProductOrderList(order);
        return getDataTable(list);
    }

    /**
     * 获取订单详细信息
     */
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrder order = productOrderService.selectProductOrderById(orderId);
        if (order == null || (!userId.equals(order.getUserId()) && !userId.equals(order.getSellerId())))
        {
            return error("订单不存在或无权限访问");
        }
        return success(order);
    }

    /**
     * 根据订单号获取订单信息
     */
    @GetMapping("/no/{orderNo}")
    public AjaxResult getInfoByOrderNo(@PathVariable String orderNo)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrder order = productOrderService.selectProductOrderByOrderNo(orderNo);
        if (order == null || (!userId.equals(order.getUserId()) && !userId.equals(order.getSellerId())))
        {
            return error("订单不存在或无权限访问");
        }
        return success(order);
    }

    /**
     * 支付订单（模拟支付）
     */
    @PostMapping("/{orderNo}/pay")
    public AjaxResult payOrder(@PathVariable String orderNo, @RequestBody Map<String, String> params)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrder order = productOrderService.selectProductOrderByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId))
        {
            return error("订单不存在或无权限操作");
        }
        
        String payMethod = params.get("payMethod");
        if (payMethod == null || payMethod.isEmpty())
        {
            return error("请选择支付方式");
        }
        
        try
        {
            int result = productOrderService.payOrder(orderNo, payMethod);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderNo}/cancel")
    public AjaxResult cancelOrder(@PathVariable String orderNo, @RequestBody Map<String, String> params)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrder order = productOrderService.selectProductOrderByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId))
        {
            return error("订单不存在或无权限操作");
        }
        
        String cancelReason = params.get("cancelReason");
        if (cancelReason == null || cancelReason.trim().isEmpty())
        {
            return error("请填写取消原因");
        }
        
        try
        {
            int result = productOrderService.cancelOrder(orderNo, cancelReason);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @PostMapping("/{orderNo}/confirm")
    public AjaxResult confirmReceive(@PathVariable String orderNo)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrder order = productOrderService.selectProductOrderByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId))
        {
            return error("订单不存在或无权限操作");
        }
        
        try
        {
            int result = productOrderService.confirmReceive(orderNo);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 卖家发货
     */
    @PostMapping("/{orderNo}/ship")
    public AjaxResult shipOrder(@PathVariable String orderNo, @RequestBody Map<String, String> params)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrder order = productOrderService.selectProductOrderByOrderNo(orderNo);
        if (order == null || !order.getSellerId().equals(userId))
        {
            return error("订单不存在或无权限操作");
        }
        
        String expressCompany = params.get("expressCompany");
        String expressNo = params.get("expressNo");
        
        if (expressCompany == null || expressCompany.trim().isEmpty())
        {
            return error("请填写快递公司");
        }
        
        if (expressNo == null || expressNo.trim().isEmpty())
        {
            return error("请填写快递单号");
        }
        
        try
        {
            int result = productOrderService.shipOrder(orderNo, expressCompany, expressNo);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 申请退款/退货
     */
    @PostMapping("/{orderId}/return")
    public AjaxResult applyReturn(@PathVariable Long orderId, @RequestBody Map<String, String> params)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrder order = productOrderService.selectProductOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId))
        {
            return error("订单不存在或无权限操作");
        }
        
        String returnType = params.get("returnType"); // refund仅退款, return_refund退货退款
        String returnReason = params.get("returnReason");
        String returnDescription = params.get("returnDescription");
        String returnImages = params.get("returnImages");
        String receiveStatus = params.get("receiveStatus"); // received / not_received
        
        if (returnType == null || returnType.trim().isEmpty())
        {
            return error("请选择退款类型");
        }
        if (returnReason == null || returnReason.trim().isEmpty())
        {
            return error("请填写退款原因");
        }
        
        try
        {
            ProductOrderReturn orderReturn = productOrderReturnService.applyReturn(
                orderId, returnType, returnReason, returnDescription, returnImages, receiveStatus);
            return success(orderReturn);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 查询订单的退款/退货记录
     */
    @GetMapping("/{orderId}/returns")
    public AjaxResult getOrderReturns(@PathVariable Long orderId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrder order = productOrderService.selectProductOrderById(orderId);
        if (order == null || (!userId.equals(order.getUserId()) && !userId.equals(order.getSellerId())))
        {
            return error("订单不存在或无权限访问");
        }
        
        List<ProductOrderReturn> returns = productOrderReturnService.selectProductOrderReturnListByOrderId(orderId);
        return success(returns);
    }

    /**
     * 获取退款/退货详情
     */
    @GetMapping("/return/{returnId}")
    public AjaxResult getReturnInfo(@PathVariable Long returnId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退款申请不存在");
        }
        
        // 去除权限检查，允许查看退款详情
        return success(orderReturn);
    }

    /**
     * 填写退货物流信息
     */
    @PostMapping("/return/{returnId}/shipping")
    public AjaxResult fillReturnShipping(@PathVariable Long returnId, @RequestBody Map<String, String> params)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退款申请不存在");
        }
        
        ProductOrder order = productOrderService.selectProductOrderById(orderReturn.getOrderId());
        if (order == null || !order.getUserId().equals(userId))
        {
            return error("无权限操作");
        }
        
        String expressCompany = params.get("expressCompany");
        String expressNo = params.get("expressNo");
        
        if (expressCompany == null || expressCompany.trim().isEmpty())
        {
            return error("请填写快递公司");
        }
        if (expressNo == null || expressNo.trim().isEmpty())
        {
            return error("请填写快递单号");
        }
        
        try
        {
            int result = productOrderReturnService.fillReturnShipping(returnId, expressCompany, expressNo);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 取消退款/退货申请
     */
    @PostMapping("/return/{returnId}/cancel")
    public AjaxResult cancelReturn(@PathVariable Long returnId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退款申请不存在");
        }
        
        ProductOrder order = productOrderService.selectProductOrderById(orderReturn.getOrderId());
        if (order == null || !order.getUserId().equals(userId))
        {
            return error("无权限操作");
        }
        
        try
        {
            int result = productOrderReturnService.cancelReturn(returnId);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }
}

