package com.secondhand.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.secondhand.system.domain.ProductCategory;

/**
 * 商品类别Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
public interface ProductCategoryMapper 
{
    /**
     * 查询商品类别
     * 
     * @param categoryId 商品类别主键
     * @return 商品类别
     */
    public ProductCategory selectProductCategoryByCategoryId(Long categoryId);

    /**
     * 查询商品类别列表
     * 
     * @param productCategory 商品类别
     * @return 商品类别集合
     */
    public List<ProductCategory> selectProductCategoryList(ProductCategory productCategory);

    /**
     * 新增商品类别
     * 
     * @param productCategory 商品类别
     * @return 结果
     */
    public int insertProductCategory(ProductCategory productCategory);

    /**
     * 修改商品类别
     * 
     * @param productCategory 商品类别
     * @return 结果
     */
    public int updateProductCategory(ProductCategory productCategory);

    /**
     * 删除商品类别
     * 
     * @param categoryId 商品类别主键
     * @return 结果
     */
    public int deleteProductCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除商品类别
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProductCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 校验同级名称唯一
     *
     * @param categoryName 名称
     * @param parentId 上级
     * @return 类别
     */
    public ProductCategory checkProductCategoryNameUnique(@Param("categoryName") String categoryName, @Param("parentId") Long parentId);

    /**
     * 查询子节点
     *
     * @param categoryId 当前ID
     * @return 子级列表
     */
    public List<ProductCategory> selectChildrenProductCategoryById(Long categoryId);

    /**
     * 批量更新子节点的祖级链
     *
     * @param categories 子节点
     * @return 结果
     */
    public int updateProductCategoryChildren(List<ProductCategory> categories);

    /**
     * 是否存在子节点
     * @param categoryId 类别ID
     * @return 结果
     */
    public int hasChildByCategoryId(Long categoryId);
}
