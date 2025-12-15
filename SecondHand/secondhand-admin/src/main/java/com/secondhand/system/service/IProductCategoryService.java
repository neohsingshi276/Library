package com.secondhand.system.service;

import java.util.List;
import com.secondhand.system.domain.ProductCategory;
import com.secondhand.common.core.domain.TreeSelect;

/**
 * 商品类别Service接口
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
public interface IProductCategoryService 
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
     * 批量删除商品类别
     * 
     * @param categoryIds 需要删除的商品类别主键集合
     * @return 结果
     */
    public int deleteProductCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 删除商品类别信息
     * 
     * @param categoryId 商品类别主键
     * @return 结果
     */
    public int deleteProductCategoryByCategoryId(Long categoryId);

    /**
     * 校验类别名称唯一
     *
     * @param productCategory 商品类别
     * @return UNIQUE/NOT_UNIQUE
     */
    public boolean checkCategoryNameUnique(ProductCategory productCategory);

    /**
     * 构建类别树
     *
     * @param categories 类别列表
     * @return 树
     */
    public List<ProductCategory> buildCategoryTree(List<ProductCategory> categories);

    /**
     * 构建下拉树结构
     *
     * @param categories 类别列表
     * @return 下拉树
     */
    public List<TreeSelect> buildCategoryTreeSelect(List<ProductCategory> categories);

    /**
     * 是否存在子节点
     *
     * @param categoryId 类别ID
     * @return true-有子类
     */
    public boolean hasChildByCategoryId(Long categoryId);
}
