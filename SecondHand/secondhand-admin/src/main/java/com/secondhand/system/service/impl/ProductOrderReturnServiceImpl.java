package com.secondhand.system.service.impl;

import com.secondhand.common.utils.DateUtils;
import com.secondhand.system.domain.ProductOrder;
import com.secondhand.system.domain.ProductOrderReturn;
import com.secondhand.system.mapper.ProductOrderMapper;
import com.secondhand.system.mapper.ProductOrderReturnMapper;
import com.secondhand.system.service.ClientService;
import com.secondhand.system.service.IProductOrderReturnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单退款/退货Service业务层处理
 */
@Service
public class ProductOrderReturnServiceImpl implements IProductOrderReturnService
{
    private static final Logger log = LoggerFactory.getLogger(ProductOrderReturnServiceImpl.class);

    @Autowired
    private ProductOrderReturnMapper productOrderReturnMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ClientService clientService;

    @Override
    public ProductOrderReturn selectProductOrderReturnById(Long returnId)
    {
        return productOrderReturnMapper.selectProductOrderReturnById(returnId);
    }

    @Override
    public ProductOrderReturn selectProductOrderReturnByReturnNo(String returnNo)
    {
        return productOrderReturnMapper.selectProductOrderReturnByReturnNo(returnNo);
    }

    @Override
    public List<ProductOrderReturn> selectProductOrderReturnListByOrderId(Long orderId)
    {
        return productOrderReturnMapper.selectProductOrderReturnListByOrderId(orderId);
    }

    @Override
    public List<ProductOrderReturn> selectProductOrderReturnListByOrderNo(String orderNo)
    {
        return productOrderReturnMapper.selectProductOrderReturnListByOrderNo(orderNo);
    }

    @Override
    public List<ProductOrderReturn> selectProductOrderReturnList(ProductOrderReturn productOrderReturn)
    {
        return productOrderReturnMapper.selectProductOrderReturnList(productOrderReturn);
    }

    @Override
    public List<Map<String, Object>> selectProductOrderReturnListByUserId(Long userId)
    {
        return productOrderReturnMapper.selectProductOrderReturnListByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> selectProductOrderReturnListBySellerId(Long sellerId)
    {
        return productOrderReturnMapper.selectProductOrderReturnListBySellerId(sellerId);
    }

    @Override
    public int insertProductOrderReturn(ProductOrderReturn productOrderReturn)
    {
        return productOrderReturnMapper.insertProductOrderReturn(productOrderReturn);
    }

    @Override
    public int updateProductOrderReturn(ProductOrderReturn productOrderReturn)
    {
        return productOrderReturnMapper.updateProductOrderReturn(productOrderReturn);
    }

    @Override
    public int deleteProductOrderReturnById(Long returnId)
    {
        return productOrderReturnMapper.deleteProductOrderReturnById(returnId);
    }

    @Override
    public int deleteProductOrderReturnByIds(Long[] returnIds)
    {
        return productOrderReturnMapper.deleteProductOrderReturnByIds(returnIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductOrderReturn applyReturn(Long orderId, String returnType, String returnReason, String returnDescription, String returnImages, String receiveStatus)
    {
        ProductOrder order = productOrderMapper.selectProductOrderById(orderId);
        if (order == null)
        {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单状态是否允许退款
        String orderStatus = order.getOrderStatus();
        if (!"paid".equals(orderStatus) && !"shipped".equals(orderStatus) && !"completed".equals(orderStatus))
        {
            throw new RuntimeException("当前订单状态不允许申请退款");
        }

        // 待发货状态只能申请仅退款
        if ("paid".equals(orderStatus) && !"refund".equals(returnType))
        {
            throw new RuntimeException("待发货订单只能申请仅退款");
        }

        // 待收货和已完成状态只能申请退货退款
        if (("shipped".equals(orderStatus) || "completed".equals(orderStatus)) && !"return_refund".equals(returnType))
        {
            throw new RuntimeException("已发货订单只能申请退货退款");
        }

        // 已完成订单需要在15天内申请
        if ("completed".equals(orderStatus))
        {
            Date completeTime = order.getCompleteTime();
            if (completeTime != null)
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(completeTime);
                cal.add(Calendar.DAY_OF_MONTH, 15);
                if (new Date().after(cal.getTime()))
                {
                    throw new RuntimeException("订单已完成超过15天，无法申请退款");
                }
            }
        }

        // 检查是否已有进行中的退款申请
        List<ProductOrderReturn> existingReturns = productOrderReturnMapper.selectProductOrderReturnListByOrderId(orderId);
        for (ProductOrderReturn existingReturn : existingReturns)
        {
            String status = existingReturn.getReturnStatus();
            if ("requested".equals(status) || "approved".equals(status) || "shipped".equals(status))
            {
                throw new RuntimeException("该订单已有进行中的退款申请");
            }
        }

        // 创建退款/退货记录
        ProductOrderReturn orderReturn = new ProductOrderReturn();
        orderReturn.setOrderId(orderId);
        orderReturn.setOrderNo(order.getOrderNo());
        orderReturn.setReturnNo(generateReturnNo());
        orderReturn.setReturnType(returnType);
        orderReturn.setReturnReason(returnReason);
        orderReturn.setReturnDescription(returnDescription);
        orderReturn.setReturnImages(returnImages);
        orderReturn.setReturnStatus("requested");
        orderReturn.setReceiveStatus(receiveStatus);
        orderReturn.setReturnAmount(order.getOrderAmount());
        orderReturn.setReturnShippingFee(BigDecimal.ZERO);
        
        // 设置卖家处理截止时间（48小时后）
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.HOUR_OF_DAY, 48);
        orderReturn.setApproveDeadline(deadline.getTime());
        
        orderReturn.setCreateTime(DateUtils.getNowDate());
        orderReturn.setUpdateTime(DateUtils.getNowDate());

        productOrderReturnMapper.insertProductOrderReturn(orderReturn);

        // 更新订单状态
        order.setOrderStatus("return_requested");
        order.setUpdateTime(DateUtils.getNowDate());
        productOrderMapper.updateProductOrder(order);

        // 用户申请退款，扣除买家信用分2分
        clientService.decreaseCreditScore(order.getUserId(), 2);

        return orderReturn;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveReturn(Long returnId)
    {
        ProductOrderReturn orderReturn = productOrderReturnMapper.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            throw new RuntimeException("退款申请不存在");
        }

        if (!"requested".equals(orderReturn.getReturnStatus()))
        {
            throw new RuntimeException("当前状态不允许此操作");
        }

        // 更新退款状态
        orderReturn.setReturnStatus("approved");
        orderReturn.setApproveTime(DateUtils.getNowDate());
        
        // 如果是退货退款，设置买家寄回截止时间（7天后）
        if ("return_refund".equals(orderReturn.getReturnType()))
        {
            Calendar shippingDeadline = Calendar.getInstance();
            shippingDeadline.add(Calendar.DAY_OF_MONTH, 7);
            orderReturn.setShippingDeadline(shippingDeadline.getTime());
        }
        // 仅退款流程：设置为approved状态，等待卖家手动退款
        // 这样前端可以在approved状态显示"确认退款"按钮，调用refundReturn接口完成退款
        
        orderReturn.setUpdateTime(DateUtils.getNowDate());
        productOrderReturnMapper.updateProductOrderReturn(orderReturn);

        // 更新订单状态为return_requested（退货流程进行中）
        ProductOrder order = productOrderMapper.selectProductOrderById(orderReturn.getOrderId());
        if (order != null)
        {
            // 确保订单状态更新为return_requested，除非已经是最终状态（refunded/returned）
            String currentStatus = order.getOrderStatus();
            if (!"return_requested".equals(currentStatus) && 
                !"refunded".equals(currentStatus) && 
                !"returned".equals(currentStatus))
            {
                order.setOrderStatus("return_requested");
                order.setUpdateTime(DateUtils.getNowDate());
                productOrderMapper.updateProductOrder(order);
                log.info("同意退货申请，订单ID: {} 状态已更新为return_requested", order.getOrderId());
            }
        }

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejectReturn(Long returnId, String rejectReason)
    {
        ProductOrderReturn orderReturn = productOrderReturnMapper.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            throw new RuntimeException("退款申请不存在");
        }

        if (!"requested".equals(orderReturn.getReturnStatus()))
        {
            throw new RuntimeException("当前状态不允许此操作");
        }

        // 更新退款状态
        orderReturn.setReturnStatus("rejected");
        orderReturn.setRejectReason(rejectReason);
        orderReturn.setRejectTime(DateUtils.getNowDate());
        orderReturn.setUpdateTime(DateUtils.getNowDate());
        productOrderReturnMapper.updateProductOrderReturn(orderReturn);

        // 恢复订单状态到申请前的状态
        ProductOrder order = productOrderMapper.selectProductOrderById(orderReturn.getOrderId());
        if (order != null)
        {
            String currentStatus = order.getOrderStatus();
            // 只有当订单状态是return_requested时才需要恢复
            if ("return_requested".equals(currentStatus))
            {
                String restoredStatus = null;
                // 根据订单的支付状态和发货状态恢复到申请前的状态
                String payStatus = order.getPayStatus();
                String shippingStatus = order.getShippingStatus();
                
                // 已支付但未发货 -> paid
                if ("1".equals(payStatus) && ("0".equals(shippingStatus) || shippingStatus == null))
                {
                    restoredStatus = "paid";
                }
                // 已发货但未确认收货 -> shipped
                else if ("1".equals(shippingStatus))
                {
                    // 检查是否有完成时间，如果有说明已确认收货
                    if (order.getCompleteTime() != null)
                    {
                        restoredStatus = "completed";
                    }
                    else
                    {
                        restoredStatus = "shipped";
                    }
                }
                // 已确认收货 -> completed
                else if (order.getCompleteTime() != null)
                {
                    restoredStatus = "completed";
                }
                // 默认恢复到paid状态（已支付但未发货）
                else
                {
                    restoredStatus = "paid";
                }
                
                if (restoredStatus != null)
                {
                    order.setOrderStatus(restoredStatus);
                    order.setUpdateTime(DateUtils.getNowDate());
                    productOrderMapper.updateProductOrder(order);
                    log.info("拒绝退货申请，订单ID: {} 状态已恢复为: {}", order.getOrderId(), restoredStatus);
                }
            }

            // 商家拒绝退款，扣除卖家信用分5分
            clientService.decreaseCreditScore(order.getSellerId(), 5);
        }

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int fillReturnShipping(Long returnId, String expressCompany, String expressNo)
    {
        ProductOrderReturn orderReturn = productOrderReturnMapper.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            throw new RuntimeException("退款申请不存在");
        }

        if (!"approved".equals(orderReturn.getReturnStatus()))
        {
            throw new RuntimeException("当前状态不允许填写物流信息");
        }

        if (!"return_refund".equals(orderReturn.getReturnType()))
        {
            throw new RuntimeException("仅退款无需填写物流信息");
        }

        // 检查是否超过寄回截止时间
        if (orderReturn.getShippingDeadline() != null && new Date().after(orderReturn.getShippingDeadline()))
        {
            throw new RuntimeException("已超过寄回截止时间，无法填写物流信息");
        }

        // 更新物流信息
        orderReturn.setReturnExpressCompany(expressCompany);
        orderReturn.setReturnExpressNo(expressNo);
        orderReturn.setReturnShippingTime(DateUtils.getNowDate());
        orderReturn.setReturnStatus("shipped");
        orderReturn.setUpdateTime(DateUtils.getNowDate());
        productOrderReturnMapper.updateProductOrderReturn(orderReturn);

        // 更新订单状态（保持return_requested状态，因为退货流程还在进行中）
        ProductOrder order = productOrderMapper.selectProductOrderById(orderReturn.getOrderId());
        if (order != null)
        {
            // 确保订单状态为return_requested（退货流程进行中）
            String currentStatus = order.getOrderStatus();
            if (!"return_requested".equals(currentStatus) && 
                !"refunded".equals(currentStatus) && 
                !"returned".equals(currentStatus))
            {
                order.setOrderStatus("return_requested");
                order.setUpdateTime(DateUtils.getNowDate());
                productOrderMapper.updateProductOrder(order);
                log.info("买家填写退货物流，订单ID: {} 状态已更新为return_requested", order.getOrderId());
            }
        }

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmReturnReceive(Long returnId)
    {
        ProductOrderReturn orderReturn = productOrderReturnMapper.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            throw new RuntimeException("退款申请不存在");
        }

        if (!"shipped".equals(orderReturn.getReturnStatus()))
        {
            throw new RuntimeException("当前状态不允许确认收货");
        }

        Date now = DateUtils.getNowDate();

        // 更新退款状态为received（卖家已确认收货，等待退款）
        orderReturn.setReturnStatus("received");
        orderReturn.setSellerReceiveTime(now);
        orderReturn.setUpdateTime(now);
        productOrderReturnMapper.updateProductOrderReturn(orderReturn);

        // 更新订单状态（保持return_requested状态，因为等待退款）
        ProductOrder order = productOrderMapper.selectProductOrderById(orderReturn.getOrderId());
        if (order != null)
        {
            // 确保订单状态为return_requested（退货流程进行中，等待退款）
            String currentStatus = order.getOrderStatus();
            if (!"return_requested".equals(currentStatus) && 
                !"refunded".equals(currentStatus) && 
                !"returned".equals(currentStatus))
            {
                order.setOrderStatus("return_requested");
                order.setUpdateTime(now);
                productOrderMapper.updateProductOrder(order);
                log.info("卖家确认收到退货，订单ID: {} 状态已更新为return_requested", order.getOrderId());
            }
        }

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelReturn(Long returnId)
    {
        ProductOrderReturn orderReturn = productOrderReturnMapper.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            throw new RuntimeException("退款申请不存在");
        }

        String status = orderReturn.getReturnStatus();
        if (!"requested".equals(status) && !"approved".equals(status))
        {
            throw new RuntimeException("当前状态不允许取消");
        }

        // 更新退款状态
        orderReturn.setReturnStatus("cancelled");
        orderReturn.setCancelTime(DateUtils.getNowDate());
        orderReturn.setUpdateTime(DateUtils.getNowDate());
        productOrderReturnMapper.updateProductOrderReturn(orderReturn);

        // 恢复订单状态
        ProductOrder order = productOrderMapper.selectProductOrderById(orderReturn.getOrderId());
        if (order != null)
        {
            if ("1".equals(order.getPayStatus()) && "0".equals(order.getShippingStatus()))
            {
                order.setOrderStatus("paid");
            }
            else if ("1".equals(order.getShippingStatus()))
            {
                order.setOrderStatus("shipped");
            }
            else
            {
                order.setOrderStatus("completed");
            }
            order.setUpdateTime(DateUtils.getNowDate());
            productOrderMapper.updateProductOrder(order);
        }

        return 1;
    }

    @Override
    public String generateReturnNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000; // 4位随机数
        return "RET" + dateStr + randomNum;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int refundReturn(Long returnId)
    {
        ProductOrderReturn orderReturn = productOrderReturnMapper.selectProductOrderReturnById(returnId);
        if (orderReturn == null)
        {
            throw new RuntimeException("退款申请不存在");
        }

        String returnStatus = orderReturn.getReturnStatus();
        String returnType = orderReturn.getReturnType();
        
        // 处理两种情况：
        // 1. 仅退款且状态为approved
        // 2. 退货退款且状态为received（卖家已确认收货，等待退款）
        if (!(("approved".equals(returnStatus) && "refund".equals(returnType)) || 
              ("received".equals(returnStatus) && "return_refund".equals(returnType))))
        {
            throw new RuntimeException("当前状态不允许退款，状态：" + returnStatus + "，类型：" + returnType);
        }

        Date now = DateUtils.getNowDate();

        // 更新退款状态为已完成并记录退款时间与金额
        orderReturn.setReturnStatus("completed");
        orderReturn.setCompleteTime(now);
        orderReturn.setRefundTime(now);
        if (orderReturn.getRefundAmount() == null)
        {
            orderReturn.setRefundAmount(orderReturn.getReturnAmount());
        }
        orderReturn.setUpdateTime(now);
        productOrderReturnMapper.updateProductOrderReturn(orderReturn);

        // 更新订单状态为已退款或已退货
        ProductOrder order = productOrderMapper.selectProductOrderById(orderReturn.getOrderId());
        if (order != null)
        {
            if ("return_refund".equals(returnType))
            {
                order.setOrderStatus("returned");
            }
            else
            {
                order.setOrderStatus("refunded");
            }
            order.setUpdateTime(now);
            productOrderMapper.updateProductOrder(order);
        }

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processTimeoutReturns()
    {
        // 查询所有申请中且超过48小时未处理的退款申请
        List<ProductOrderReturn> timeoutReturns = productOrderReturnMapper.selectProductOrderReturnList(
            new ProductOrderReturn() {{
                setReturnStatus("requested");
            }}
        );

        Date now = new Date();
        for (ProductOrderReturn orderReturn : timeoutReturns)
        {
            if (orderReturn.getApproveDeadline() != null && now.after(orderReturn.getApproveDeadline()))
            {
                // 自动同意退款
                try
                {
                    orderReturn.setApproveTime(now);
                    
                    // 如果是仅退款，超时自动同意后直接完成退款（自动处理，无需卖家再确认）
                    if ("refund".equals(orderReturn.getReturnType()))
                    {
                        orderReturn.setReturnStatus("completed");
                        orderReturn.setCompleteTime(now);
                        orderReturn.setRefundTime(now);
                        if (orderReturn.getRefundAmount() == null)
                        {
                            orderReturn.setRefundAmount(orderReturn.getReturnAmount());
                        }
                        
                        // 更新订单状态为已退款（不是已取消）
                        ProductOrder order = productOrderMapper.selectProductOrderById(orderReturn.getOrderId());
                        if (order != null)
                        {
                            order.setOrderStatus("refunded");
                            order.setUpdateTime(now);
                            productOrderMapper.updateProductOrder(order);
                        }
                    }
                    else
                    {
                        // 退货退款：设置为approved状态（等待买家填写物流），设置寄回截止时间
                        orderReturn.setReturnStatus("approved");
                        Calendar shippingDeadline = Calendar.getInstance();
                        shippingDeadline.add(Calendar.DAY_OF_MONTH, 7);
                        orderReturn.setShippingDeadline(shippingDeadline.getTime());
                    }
                    
                    orderReturn.setUpdateTime(now);
                    productOrderReturnMapper.updateProductOrderReturn(orderReturn);
                    
                    log.info("自动同意超时退款申请: {}", orderReturn.getReturnNo());
                }
                catch (Exception e)
                {
                    log.error("处理超时退款申请失败: {}", orderReturn.getReturnNo(), e);
                }
            }
        }
    }
}

