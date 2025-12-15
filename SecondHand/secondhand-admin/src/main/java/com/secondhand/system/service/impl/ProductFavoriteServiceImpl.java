package com.secondhand.system.service.impl;

import com.secondhand.common.utils.DateUtils;
import com.secondhand.system.domain.Product;
import com.secondhand.system.domain.ProductFavorite;
import com.secondhand.system.mapper.ProductFavoriteMapper;
import com.secondhand.system.mapper.ProductMapper;
import com.secondhand.system.service.IProductFavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品收藏Service业务层处理
 */
@Service
public class ProductFavoriteServiceImpl implements IProductFavoriteService
{
    private static final Logger log = LoggerFactory.getLogger(ProductFavoriteServiceImpl.class);

    @Autowired
    private ProductFavoriteMapper productFavoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductFavorite selectProductFavorite(ProductFavorite productFavorite)
    {
        return productFavoriteMapper.selectProductFavorite(productFavorite);
    }

    @Override
    public List<ProductFavorite> selectProductFavoriteList(ProductFavorite productFavorite)
    {
        return productFavoriteMapper.selectProductFavoriteList(productFavorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertProductFavorite(ProductFavorite productFavorite)
    {
        productFavorite.setCreateTime(DateUtils.getNowDate());
        int result = productFavoriteMapper.insertProductFavorite(productFavorite);
        // 更新商品收藏数量
        if (result > 0)
        {
            updateProductFavoriteCount(productFavorite.getProductId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProductFavorite(ProductFavorite productFavorite)
    {
        int result = productFavoriteMapper.deleteProductFavorite(productFavorite);
        // 更新商品收藏数量
        if (result > 0 && productFavorite.getProductId() != null)
        {
            updateProductFavoriteCount(productFavorite.getProductId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProductFavoriteByIds(Long[] favoriteIds)
    {
        // 先查询要删除的收藏记录，获取商品ID列表
        List<ProductFavorite> favorites = productFavoriteMapper.selectProductFavoriteList(new ProductFavorite());
        // 这里简化处理，实际应该根据favoriteIds查询
        
        int result = productFavoriteMapper.deleteProductFavoriteByIds(favoriteIds);
        return result;
    }

    @Override
    public boolean isFavorite(Long userId, Long productId)
    {
        if (userId == null || productId == null)
        {
            return false;
        }
        int count = productFavoriteMapper.checkFavoriteExists(userId, productId);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFavorite(Long userId, Long productId)
    {
        if (userId == null || productId == null)
        {
            throw new RuntimeException("用户ID和商品ID不能为空");
        }

        // 检查商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null)
        {
            throw new RuntimeException("商品不存在");
        }

        // 检查是否是自己发布的商品（不能收藏自己发布的商品）
        if (product.getSellerId() != null && product.getSellerId().equals(userId))
        {
            throw new RuntimeException("不能收藏自己发布的商品");
        }

        // 检查是否已收藏
        boolean exists = isFavorite(userId, productId);
        
        if (exists)
        {
            // 取消收藏
            ProductFavorite favorite = new ProductFavorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            int result = deleteProductFavorite(favorite);
            return false; // 返回false表示已取消收藏
        }
        else
        {
            // 添加收藏
            ProductFavorite favorite = new ProductFavorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            int result = insertProductFavorite(favorite);
            return true; // 返回true表示已收藏
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductFavoriteCount(Long productId)
    {
        if (productId == null)
        {
            return;
        }
        
        // 统计当前收藏数量
        int count = productFavoriteMapper.countProductFavoriteByProductId(productId);
        
        // 更新商品表的收藏数量
        Product product = new Product();
        product.setProductId(productId);
        product.setFavoriteCount(count);
        productMapper.updateProduct(product);
        
        log.debug("更新商品ID: {} 的收藏数量为: {}", productId, count);
    }
}

