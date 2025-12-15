package com.secondhand.system.service.impl;

import com.secondhand.common.exception.ServiceException;
import com.secondhand.common.utils.DateUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.system.domain.Product;
import com.secondhand.system.domain.ProductExchange;
import com.secondhand.system.mapper.ProductExchangeMapper;
import com.secondhand.system.mapper.ProductMapper;
import com.secondhand.system.service.IProductExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 商品交换Service业务层处理
 */
@Service
public class ProductExchangeServiceImpl implements IProductExchangeService
{
    private static final Logger log = LoggerFactory.getLogger(ProductExchangeServiceImpl.class);

    @Autowired
    private ProductExchangeMapper productExchangeMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 生成交换单号
     */
    private String generateExchangeNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        Random random = new Random();
        int randomNum = random.nextInt(900000) + 100000; // 6位随机数
        return "EX" + dateStr + randomNum;
    }

    @Override
    public ProductExchange selectProductExchangeById(Long exchangeId)
    {
        return productExchangeMapper.selectProductExchangeById(exchangeId);
    }

    @Override
    public List<ProductExchange> selectProductExchangeList(ProductExchange productExchange)
    {
        return productExchangeMapper.selectProductExchangeList(productExchange);
    }

    @Override
    public List<ProductExchange> selectMyExchangeRequests(Long requesterId)
    {
        return productExchangeMapper.selectMyExchangeRequests(requesterId);
    }

    @Override
    public List<ProductExchange> selectMyExchangeReceives(Long receiverId)
    {
        return productExchangeMapper.selectMyExchangeReceives(receiverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createExchangeRequest(Long requesterId, Long receiverProductId, Long requesterProductId)
    {
        // 1. 验证发起者商品
        Product requesterProduct = productMapper.selectProductById(requesterProductId);
        if (requesterProduct == null)
        {
            throw new ServiceException("您选择的商品不存在");
        }
        if (!requesterProduct.getSellerId().equals(requesterId))
        {
            throw new ServiceException("您只能选择自己发布的商品进行交换");
        }
        if (!"published".equals(requesterProduct.getStatus()))
        {
            throw new ServiceException("您选择的商品必须是已上架状态");
        }

        // 2. 验证接收者商品
        Product receiverProduct = productMapper.selectProductById(receiverProductId);
        if (receiverProduct == null)
        {
            throw new ServiceException("目标商品不存在");
        }
        if (!"published".equals(receiverProduct.getStatus()))
        {
            throw new ServiceException("目标商品已下架或不可交换");
        }
        if (receiverProduct.getSellerId().equals(requesterId))
        {
            throw new ServiceException("不能向自己发起交换请求");
        }

        // 3. 检查是否已存在相同的pending状态交换请求
        int duplicateCount = productExchangeMapper.checkDuplicateExchange(
            requesterId, receiverProduct.getSellerId(), requesterProductId, receiverProductId);
        if (duplicateCount > 0)
        {
            throw new ServiceException("您已向该商品发起过交换请求，请勿重复提交");
        }

        // 4. 创建交换请求
        ProductExchange exchange = new ProductExchange();
        exchange.setExchangeNo(generateExchangeNo());
        exchange.setRequesterId(requesterId);
        exchange.setReceiverId(receiverProduct.getSellerId());
        exchange.setRequesterProductId(requesterProductId);
        exchange.setReceiverProductId(receiverProductId);
        exchange.setExchangeStatus("pending");
        exchange.setContactRevealed("0");
        exchange.setRequesterComplete("0");
        exchange.setReceiverComplete("0");
        exchange.setCreateTime(DateUtils.getNowDate());

        return productExchangeMapper.insertProductExchange(exchange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int acceptExchange(Long exchangeId, Long receiverId)
    {
        ProductExchange exchange = productExchangeMapper.selectProductExchangeById(exchangeId);
        if (exchange == null)
        {
            throw new ServiceException("交换请求不存在");
        }
        if (!exchange.getReceiverId().equals(receiverId))
        {
            throw new ServiceException("您无权操作此交换请求");
        }
        if (!"pending".equals(exchange.getExchangeStatus()))
        {
            throw new ServiceException("交换请求状态不正确，无法接受");
        }

        // 验证商品状态
        Product requesterProduct = productMapper.selectProductById(exchange.getRequesterProductId());
        Product receiverProduct = productMapper.selectProductById(exchange.getReceiverProductId());
        if (requesterProduct == null || receiverProduct == null)
        {
            throw new ServiceException("商品不存在");
        }
        if (!"published".equals(requesterProduct.getStatus()) || !"published".equals(receiverProduct.getStatus()))
        {
            throw new ServiceException("商品状态已变更，无法接受交换");
        }

        // 更新交换状态
        exchange.setExchangeStatus("accepted");
        exchange.setAcceptTime(DateUtils.getNowDate());
        exchange.setContactRevealed("1");
        exchange.setContactRevealTime(DateUtils.getNowDate());
        exchange.setUpdateTime(DateUtils.getNowDate());

        // 更新商品状态为"已售完"
        requesterProduct.setStatus("sold");
        receiverProduct.setStatus("sold");
        productMapper.updateProduct(requesterProduct);
        productMapper.updateProduct(receiverProduct);

        return productExchangeMapper.updateProductExchange(exchange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejectExchange(Long exchangeId, Long receiverId, String rejectReason)
    {
        ProductExchange exchange = productExchangeMapper.selectProductExchangeById(exchangeId);
        if (exchange == null)
        {
            throw new ServiceException("交换请求不存在");
        }
        if (!exchange.getReceiverId().equals(receiverId))
        {
            throw new ServiceException("您无权操作此交换请求");
        }
        if (!"pending".equals(exchange.getExchangeStatus()))
        {
            throw new ServiceException("交换请求状态不正确，无法拒绝");
        }

        exchange.setExchangeStatus("rejected");
        exchange.setRejectReason(rejectReason);
        exchange.setRejectTime(DateUtils.getNowDate());
        exchange.setUpdateTime(DateUtils.getNowDate());

        return productExchangeMapper.updateProductExchange(exchange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int requesterComplete(Long exchangeId, Long requesterId)
    {
        ProductExchange exchange = productExchangeMapper.selectProductExchangeById(exchangeId);
        if (exchange == null)
        {
            throw new ServiceException("交换请求不存在");
        }
        if (!exchange.getRequesterId().equals(requesterId))
        {
            throw new ServiceException("您无权操作此交换请求");
        }
        if (!"accepted".equals(exchange.getExchangeStatus()))
        {
            throw new ServiceException("交换请求状态不正确，无法完成");
        }

        exchange.setRequesterComplete("1");
        exchange.setUpdateTime(DateUtils.getNowDate());

        // 检查是否双方都已完成
        if ("1".equals(exchange.getReceiverComplete()))
        {
            exchange.setExchangeStatus("completed");
            exchange.setCompleteTime(DateUtils.getNowDate());

            // 更新商品状态为"已交换"
            Product requesterProduct = productMapper.selectProductById(exchange.getRequesterProductId());
            Product receiverProduct = productMapper.selectProductById(exchange.getReceiverProductId());
            if (requesterProduct != null)
            {
                requesterProduct.setStatus("exchanged");
                productMapper.updateProduct(requesterProduct);
            }
            if (receiverProduct != null)
            {
                receiverProduct.setStatus("exchanged");
                productMapper.updateProduct(receiverProduct);
            }
        }

        return productExchangeMapper.updateProductExchange(exchange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int receiverComplete(Long exchangeId, Long receiverId)
    {
        ProductExchange exchange = productExchangeMapper.selectProductExchangeById(exchangeId);
        if (exchange == null)
        {
            throw new ServiceException("交换请求不存在");
        }
        if (!exchange.getReceiverId().equals(receiverId))
        {
            throw new ServiceException("您无权操作此交换请求");
        }
        if (!"accepted".equals(exchange.getExchangeStatus()))
        {
            throw new ServiceException("交换请求状态不正确，无法完成");
        }

        exchange.setReceiverComplete("1");
        exchange.setUpdateTime(DateUtils.getNowDate());

        // 检查是否双方都已完成
        if ("1".equals(exchange.getRequesterComplete()))
        {
            exchange.setExchangeStatus("completed");
            exchange.setCompleteTime(DateUtils.getNowDate());

            // 更新商品状态为"已交换"
            Product requesterProduct = productMapper.selectProductById(exchange.getRequesterProductId());
            Product receiverProduct = productMapper.selectProductById(exchange.getReceiverProductId());
            if (requesterProduct != null)
            {
                requesterProduct.setStatus("exchanged");
                productMapper.updateProduct(requesterProduct);
            }
            if (receiverProduct != null)
            {
                receiverProduct.setStatus("exchanged");
                productMapper.updateProduct(receiverProduct);
            }
        }

        return productExchangeMapper.updateProductExchange(exchange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelExchange(Long exchangeId, Long userId, String cancelReason)
    {
        ProductExchange exchange = productExchangeMapper.selectProductExchangeById(exchangeId);
        if (exchange == null)
        {
            throw new ServiceException("交换请求不存在");
        }
        if (!exchange.getRequesterId().equals(userId) && !exchange.getReceiverId().equals(userId))
        {
            throw new ServiceException("您无权操作此交换请求");
        }
        if ("completed".equals(exchange.getExchangeStatus()) || "cancelled".equals(exchange.getExchangeStatus()))
        {
            throw new ServiceException("交换请求状态不正确，无法取消");
        }

        exchange.setExchangeStatus("cancelled");
        exchange.setCancelReason(cancelReason);
        // 保存原始状态用于恢复商品
        String originalStatus = exchange.getExchangeStatus();
        
        exchange.setExchangeStatus("cancelled");
        exchange.setCancelReason(cancelReason);
        exchange.setCancelTime(DateUtils.getNowDate());
        exchange.setUpdateTime(DateUtils.getNowDate());

        // 如果之前状态为"已接受"，商品状态为"交换中"，恢复为"已上架"
        if ("accepted".equals(originalStatus))
        {
            Product requesterProduct = productMapper.selectProductById(exchange.getRequesterProductId());
            Product receiverProduct = productMapper.selectProductById(exchange.getReceiverProductId());
            if (requesterProduct != null && "exchanging".equals(requesterProduct.getStatus()))
            {
                requesterProduct.setStatus("published");
                productMapper.updateProduct(requesterProduct);
            }
            if (receiverProduct != null && "exchanging".equals(receiverProduct.getStatus()))
            {
                receiverProduct.setStatus("published");
                productMapper.updateProduct(receiverProduct);
            }
        }

        return productExchangeMapper.updateProductExchange(exchange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProductExchangeById(Long exchangeId)
    {
        return productExchangeMapper.deleteProductExchangeById(exchangeId);
    }
}

