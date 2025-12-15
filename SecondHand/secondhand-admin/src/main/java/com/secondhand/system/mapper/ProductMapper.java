package com.secondhand.system.mapper;

import com.secondhand.system.domain.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品表Mapper
 */
public interface ProductMapper
{
    Product selectProductById(Long productId);

    List<Product> selectProductList(Product product);

    int insertProduct(Product product);

    int updateProduct(Product product);

    int deleteProductByIds(Long[] productIds);

    int deleteProductById(Long productId);

    /**
     * 统计商品总数
     */
    Long countAllProducts();

    /**
     * 统计今日新增商品
     */
    Long countTodayProducts();

    /**
     * 按状态统计商品数量
     */
    List<Map<String, Object>> countProductsByStatus();
}


