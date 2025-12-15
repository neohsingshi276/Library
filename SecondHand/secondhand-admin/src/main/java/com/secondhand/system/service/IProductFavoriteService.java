package com.secondhand.system.service;

import com.secondhand.system.domain.ProductFavorite;

import java.util.List;

/**
 * 商品收藏Service接口
 */
public interface IProductFavoriteService
{
    /**
     * 查询收藏记录
     */
    ProductFavorite selectProductFavorite(ProductFavorite productFavorite);

    /**
     * 查询用户收藏列表
     */
    List<ProductFavorite> selectProductFavoriteList(ProductFavorite productFavorite);

    /**
     * 新增收藏
     */
    int insertProductFavorite(ProductFavorite productFavorite);

    /**
     * 删除收藏
     */
    int deleteProductFavorite(ProductFavorite productFavorite);

    /**
     * 批量删除收藏
     */
    int deleteProductFavoriteByIds(Long[] favoriteIds);

    /**
     * 检查用户是否已收藏商品
     */
    boolean isFavorite(Long userId, Long productId);

    /**
     * 切换收藏状态（收藏/取消收藏）
     */
    boolean toggleFavorite(Long userId, Long productId);

    /**
     * 更新商品收藏数量
     */
    void updateProductFavoriteCount(Long productId);
}

