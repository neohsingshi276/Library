package com.secondhand.web.controller.app;

import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.page.TableDataInfo;
import com.secondhand.system.domain.Product;
import com.secondhand.system.domain.ProductOrder;
import com.secondhand.system.domain.ProductOrderReturn;
import com.secondhand.system.service.IProductOrderReturnService;
import com.secondhand.system.service.IProductOrderService;
import com.secondhand.system.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 退货Controller（卖家视角：处理卖出商品的退货）
 */
@RestController
@RequestMapping("/app/return")
public class ReturnController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(ReturnController.class);

    @Autowired
    private IProductOrderReturnService productOrderReturnService;

    @Autowired
    private IProductOrderService productOrderService;

    @Autowired
    private IProductService productService;

    /**
     * 查询退货列表（卖家视角：查询卖出商品的退货记录）
     * 获取逻辑：全部退款信息 -> 订单id -> 订单信息 -> 商品id -> 商品信息 -> 卖家id == 当前登录id
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String returnStatus,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize)
    {
        try {
            Long sellerId = getUserId(); // 当前登录用户作为卖家
            if (sellerId == null)
            {
                return getDataTable(new ArrayList<>());
            }

            // 通过sellerId查询退货记录（卖家视角：查询卖出商品的退货）
            // 查询逻辑：全部退款信息 -> 订单id -> 订单信息 -> 商品id -> 商品信息 -> 卖家id == 当前登录id
            List<Map<String, Object>> allReturnsMap = productOrderReturnService.selectProductOrderReturnListBySellerId(sellerId);
            
            if (allReturnsMap == null)
            {
                allReturnsMap = new ArrayList<>();
            }
            
            // 调试日志
            log.info("查询卖家退货记录，sellerId: {}, 查询到记录数: {}", sellerId, allReturnsMap.size());

            // 状态过滤（支持完整的状态筛选）
            if (returnStatus != null && !returnStatus.trim().isEmpty())
            {
                final String filterStatus = returnStatus;
                // 使用前端状态进行筛选（通过mapReturnStatusToFrontend映射）
                allReturnsMap = allReturnsMap.stream()
                    .filter(returnItem -> {
                        String backendStatus = (String) returnItem.get("returnStatus");
                        String frontendStatus = mapReturnStatusToFrontend(backendStatus);
                        return filterStatus.equals(frontendStatus);
                    })
                    .collect(Collectors.toList());
            }

            // 状态映射：后端状态 -> 前端状态，并转换为前端需要的格式
            List<Map<String, Object>> mappedReturns = new ArrayList<>();
            for (Map<String, Object> returnItem : allReturnsMap)
            {
                Map<String, Object> mappedItem = new HashMap<>(returnItem);
                // 将后端状态映射为前端状态
                String backendStatus = (String) returnItem.get("returnStatus");
                String frontendStatus = mapReturnStatusToFrontend(backendStatus);
                mappedItem.put("returnStatus", frontendStatus);
                // 确保订单状态字段存在，如果不存在则设置为空字符串
                if (!mappedItem.containsKey("orderStatus")) {
                    mappedItem.put("orderStatus", "");
                }
                if (!mappedItem.containsKey("shippingStatus")) {
                    mappedItem.put("shippingStatus", "");
                }
                mappedReturns.add(mappedItem);
            }

            // 手动分页处理
            int total = mappedReturns.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<Map<String, Object>> pageList = new ArrayList<>();
            if (start < total)
            {
                pageList = mappedReturns.subList(start, end);
            }

            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(200);
            dataTable.setMsg("查询成功");
            dataTable.setRows(pageList);
            dataTable.setTotal(total);
            return dataTable;
        } catch (Exception e) {
            log.error("查询退货列表失败", e);
            return getDataTable(new ArrayList<>());
        }
    }

    /**
     * 获取退货详情（卖家视角）
     */
    @GetMapping("/{returnId}")
    public AjaxResult getInfo(@PathVariable Long returnId)
    {
        Long userId = getUserId(); // 当前登录用户
        if (userId == null)
        {
            return error("请先登录");
        }

        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退货记录不存在");
        }

        // 验证权限：允许买家和卖家查看退款详情
        ProductOrder order = productOrderService.selectProductOrderById(orderReturn.getOrderId());
        if (order == null)
        {
            return error("订单不存在");
        }
        
        // 检查当前用户是否是订单的买家或卖家
        boolean isBuyer = userId.equals(order.getUserId());
        boolean isSeller = false;
        
        // 通过商品表获取卖家ID进行验证
        Product product = productService.selectProductById(order.getProductId());
        if (product != null)
        {
            isSeller = userId.equals(product.getSellerId());
        }
        
        // 如果既不是买家也不是卖家，则无权限访问
        if (!isBuyer && !isSeller)
        {
            return error("无权限访问");
        }

        try {
            // 补充订单信息到退货记录中，方便前端显示
            orderReturn.setOrderNo(order.getOrderNo());
            // 创建Map返回，包含订单状态信息
            Map<String, Object> result = new HashMap<>();
            result.put("returnId", orderReturn.getReturnId());
            result.put("orderId", orderReturn.getOrderId());
            result.put("orderNo", orderReturn.getOrderNo() != null ? orderReturn.getOrderNo() : "");
            result.put("returnNo", orderReturn.getReturnNo() != null ? orderReturn.getReturnNo() : "");
            result.put("returnType", orderReturn.getReturnType());
            result.put("returnReason", orderReturn.getReturnReason());
            result.put("returnDescription", orderReturn.getReturnDescription());
            result.put("returnImages", orderReturn.getReturnImages());
            // 状态映射：后端状态 -> 前端状态
            String backendStatus = orderReturn.getReturnStatus();
            String frontendStatus = mapReturnStatusToFrontend(backendStatus);
            result.put("returnStatus", frontendStatus);
            result.put("returnAmount", orderReturn.getReturnAmount());
            result.put("returnShippingFee", orderReturn.getReturnShippingFee());
            result.put("returnExpressCompany", orderReturn.getReturnExpressCompany());
            result.put("returnExpressNo", orderReturn.getReturnExpressNo());
            result.put("returnShippingTime", orderReturn.getReturnShippingTime());
            result.put("approveTime", orderReturn.getApproveTime());
            result.put("approveDeadline", orderReturn.getApproveDeadline());
            result.put("rejectReason", orderReturn.getRejectReason());
            result.put("rejectTime", orderReturn.getRejectTime());
            result.put("shippingDeadline", orderReturn.getShippingDeadline());
            result.put("completeTime", orderReturn.getCompleteTime());
            result.put("cancelTime", orderReturn.getCancelTime());
            result.put("createTime", orderReturn.getCreateTime());
            result.put("updateTime", orderReturn.getUpdateTime());
            // 添加订单状态信息
            result.put("orderStatus", order.getOrderStatus() != null ? order.getOrderStatus() : "");
            result.put("shippingStatus", order.getShippingStatus() != null ? order.getShippingStatus() : "");
            result.put("orderCreateTime", order.getCreateTime());
            result.put("productTitle", order.getProductTitle() != null ? order.getProductTitle() : "");
            result.put("productImage", order.getProductImage() != null ? order.getProductImage() : "");
            result.put("orderAmount", order.getOrderAmount());

            return success(result);
        } catch (Exception e) {
            log.error("获取退货详情失败", e);
            return error("获取退货详情失败: " + e.getMessage());
        }
    }

    /**
     * 取消退货申请（注意：只有买家可以取消，卖家不能取消）
     * 这个接口保留给买家使用，卖家不应该调用此接口
     */
    @PostMapping("/{returnId}/cancel")
    public AjaxResult cancelReturn(@PathVariable Long returnId, @RequestBody(required = false) Map<String, String> params)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }

        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退货申请不存在");
        }

        // 验证权限：只有买家可以取消自己的退货申请
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

    /**
     * 填写退货物流信息（注意：只有买家可以填写，卖家不能填写）
     * 这个接口保留给买家使用，卖家不应该调用此接口
     */
    @PostMapping("/{returnId}/shipping")
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
            return error("退货申请不存在");
        }

        // 验证权限：只有买家可以填写退货物流信息
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
     * 卖家同意退货申请
     */
    @PostMapping("/{returnId}/approve")
    public AjaxResult approveReturn(@PathVariable Long returnId)
    {
        Long sellerId = getUserId(); // 当前登录用户作为卖家
        if (sellerId == null)
        {
            return error("请先登录");
        }

        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退货申请不存在");
        }

        // 验证权限：只能处理自己卖出商品的退货申请（通过商品表验证卖家ID）
        ProductOrder order = productOrderService.selectProductOrderById(orderReturn.getOrderId());
        if (order == null)
        {
            return error("订单不存在");
        }
        Product product = productService.selectProductById(order.getProductId());
        if (product == null || !product.getSellerId().equals(sellerId))
        {
            return error("无权限操作");
        }

        try
        {
            int result = productOrderReturnService.approveReturn(returnId);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 卖家拒绝退货申请
     */
    @PostMapping("/{returnId}/reject")
    public AjaxResult rejectReturn(@PathVariable Long returnId, @RequestBody Map<String, String> params)
    {
        Long sellerId = getUserId(); // 当前登录用户作为卖家
        if (sellerId == null)
        {
            return error("请先登录");
        }

        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退货申请不存在");
        }

        // 验证权限：只能处理自己卖出商品的退货申请（通过商品表验证卖家ID）
        ProductOrder order = productOrderService.selectProductOrderById(orderReturn.getOrderId());
        if (order == null)
        {
            return error("订单不存在");
        }
        Product product = productService.selectProductById(order.getProductId());
        if (product == null || !product.getSellerId().equals(sellerId))
        {
            return error("无权限操作");
        }

        String rejectReason = params.get("rejectReason");
        if (rejectReason == null || rejectReason.trim().isEmpty())
        {
            return error("请填写拒绝原因");
        }

        try
        {
            int result = productOrderReturnService.rejectReturn(returnId, rejectReason);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 卖家确认收到退货
     */
    @PostMapping("/{returnId}/confirm-receive")
    public AjaxResult confirmReturnReceive(@PathVariable Long returnId)
    {
        Long sellerId = getUserId(); // 当前登录用户作为卖家
        if (sellerId == null)
        {
            return error("请先登录");
        }

        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退货申请不存在");
        }

        // 验证权限：只能处理自己卖出商品的退货申请（通过商品表验证卖家ID）
        ProductOrder order = productOrderService.selectProductOrderById(orderReturn.getOrderId());
        if (order == null)
        {
            return error("订单不存在");
        }
        Product product = productService.selectProductById(order.getProductId());
        if (product == null || !product.getSellerId().equals(sellerId))
        {
            return error("无权限操作");
        }

        try
        {
            int result = productOrderReturnService.confirmReturnReceive(returnId);
            return toAjax(result);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 卖家退款（手动触发退款，适用于已同意但未自动退款的情况）
     */
    @PostMapping("/{returnId}/refund")
    public AjaxResult refundReturn(@PathVariable Long returnId)
    {
        Long sellerId = getUserId(); // 当前登录用户作为卖家
        if (sellerId == null)
        {
            return error("请先登录");
        }

        ProductOrderReturn orderReturn = productOrderReturnService.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            return error("退货申请不存在");
        }

        // 验证权限：只能处理自己卖出商品的退货申请（通过商品表验证卖家ID）
        ProductOrder order = productOrderService.selectProductOrderById(orderReturn.getOrderId());
        if (order == null)
        {
            return error("订单不存在");
        }
        Product product = productService.selectProductById(order.getProductId());
        if (product == null || !product.getSellerId().equals(sellerId))
        {
            return error("无权限操作");
        }

        try
        {
            // 检查状态：只有approved（仅退款）或shipped（退货退款，卖家已确认收货但未退款）状态才能退款
            String returnStatus = orderReturn.getReturnStatus();
            String returnType = orderReturn.getReturnType();
            
            if ("approved".equals(returnStatus) && "refund".equals(returnType))
            {
                // 仅退款流程：状态为approved时，调用refundReturn完成退款
                // approveReturn方法已将状态设置为approved，等待卖家手动确认退款
                int result = productOrderReturnService.refundReturn(returnId);
                return toAjax(result);
            }
            else if ("shipped".equals(returnStatus) && "return_refund".equals(returnType))
            {
                // 退货退款流程：确认收到退货（状态变为received）
                int result = productOrderReturnService.confirmReturnReceive(returnId);
                return toAjax(result);
            }
            else if ("received".equals(returnStatus) && "return_refund".equals(returnType))
            {
                // 退货退款流程：确认收到退货后，执行退款
                int result = productOrderReturnService.refundReturn(returnId);
                return toAjax(result);
            }
            else if ("completed".equals(returnStatus))
            {
                return error("退款已完成，无需重复操作");
            }
            else
            {
                return error("当前状态不允许退款，状态：" + returnStatus);
            }
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 状态映射：前端状态 -> 后端状态（用于查询过滤）
     * 完整的状态：pending、approved、shipping、received、completed、rejected、cancelled
     */
    private String mapReturnStatus(String frontendStatus)
    {
        switch (frontendStatus) {
            case "pending":
                return "requested"; // 待审核（对应后端requested和timeout_approved）
            case "approved":
                return "approved"; // 待处理
            case "shipping":
                return "shipped"; // 退货中（对应后端shipped）
            case "received":
                return "received"; // 待退款
            case "completed":
                return "completed"; // 已完成
            case "rejected":
                return "rejected"; // 已拒绝
            case "cancelled":
                return "cancelled"; // 已取消
            default:
                return null;
        }
    }

    /**
     * 状态映射：后端状态 -> 前端状态（用于返回数据）
     */
    private String mapReturnStatusToFrontend(String backendStatus)
    {
        if (backendStatus == null)
        {
            return null;
        }
        switch (backendStatus) {
            case "requested":
            case "timeout_approved":
                return "pending"; // 待审核
            case "approved":
                return "approved"; // 待退货
            case "shipped":
                return "shipping"; // 待确认收货
            case "received":
                return "received"; // 待退款（卖家已确认收货，等待退款）
            case "completed":
                return "completed"; // 已完成
            case "rejected":
                return "rejected"; // 已拒绝
            case "cancelled":
                return "cancelled"; // 已取消
            default:
                return backendStatus;
        }
    }
}

