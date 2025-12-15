package com.secondhand.system.mapper;

import com.secondhand.system.domain.ProductFavorite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品收藏Mapper接口
 */
public interface ProductFavoriteMapper
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
     * 删除收藏（根据ID）
     */
    int deleteProductFavoriteById(Long favoriteId);

    /**
     * 批量删除收藏
     */
    int deleteProductFavoriteByIds(Long[] favoriteIds);

    /**
     * 统计商品收藏数量
     */
    int countProductFavoriteByProductId(Long productId);

    /**
     * 检查用户是否已收藏商品
     */
    int checkFavoriteExists(@Param("userId") Long userId, @Param("productId") Long productId);
}

