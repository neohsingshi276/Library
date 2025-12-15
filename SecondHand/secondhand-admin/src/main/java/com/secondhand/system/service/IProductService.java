package com.secondhand.system.service;

import com.secondhand.system.domain.Product;
import com.secondhand.system.domain.LLMImageRequest;
import com.secondhand.system.domain.LLMImageResponse;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService
{
    Product selectProductById(Long productId);

    List<Product> selectProductList(Product product);

    int insertProduct(Product product);

    int updateProduct(Product product);

    int deleteProductByIds(Long[] productIds);

    int deleteProductById(Long productId);

    /**
     * AI识别+自动建类+创建商品
     */
    Product aiCreateProduct(Product product, LLMImageRequest request);

    /**
     * 仅执行AI识别并返回结果，不落库
     */
    LLMImageResponse aiDetect(LLMImageRequest request);

    /**
     * 审核商品（通过）
     */
    int auditProductPass(Long productId, String auditRemark, BigDecimal donationRatio);

    /**
     * 审核商品（拒绝）
     */
    int auditProductReject(Long productId, String auditRemark);

    /**
     * 上架商品
     */
    int onlineProduct(Long productId);

    /**
     * 下架商品
     */
    int offlineProduct(Long productId);

    /**
     * 增加商品浏览次数
     */
    int incrementViewCount(Long productId);
}

