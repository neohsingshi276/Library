package com.secondhand.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.secondhand.common.constant.UserConstants;
import com.secondhand.common.core.domain.TreeSelect;
import com.secondhand.common.utils.DateUtils;
import com.secondhand.common.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.secondhand.system.mapper.ProductCategoryMapper;
import com.secondhand.system.domain.ProductCategory;
import com.secondhand.system.service.IProductCategoryService;

/**
 * 商品类别Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
@Service
public class ProductCategoryServiceImpl implements IProductCategoryService 
{
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    /**
     * 查询商品类别
     * 
     * @param categoryId 商品类别主键
     * @return 商品类别
     */
    @Override
    public ProductCategory selectProductCategoryByCategoryId(Long categoryId)
    {
        return productCategoryMapper.selectProductCategoryByCategoryId(categoryId);
    }

    /**
     * 查询商品类别列表
     * 
     * @param productCategory 商品类别
     * @return 商品类别
     */
    @Override
    public List<ProductCategory> selectProductCategoryList(ProductCategory productCategory)
    {
        return productCategoryMapper.selectProductCategoryList(productCategory);
    }

    /**
     * 新增商品类别
     * 
     * @param productCategory 商品类别
     * @return 结果
     */
    @Override
    public int insertProductCategory(ProductCategory productCategory)
    {
        ProductCategory parent = productCategoryMapper.selectProductCategoryByCategoryId(productCategory.getParentId());
        String ancestors = StringUtils.isNotNull(parent)
                ? parent.getAncestors() + "," + parent.getCategoryId()
                : "0";
        productCategory.setAncestors(ancestors);
        productCategory.setCreateTime(DateUtils.getNowDate());
        return productCategoryMapper.insertProductCategory(productCategory);
    }

    /**
     * 修改商品类别
     * 
     * @param productCategory 商品类别
     * @return 结果
     */
    @Override
    public int updateProductCategory(ProductCategory productCategory)
    {
        ProductCategory newParent = productCategoryMapper.selectProductCategoryByCategoryId(productCategory.getParentId());
        ProductCategory oldCategory = productCategoryMapper.selectProductCategoryByCategoryId(productCategory.getCategoryId());
        String newAncestors = StringUtils.isNotNull(newParent) ? newParent.getAncestors() + "," + newParent.getCategoryId() : "0";
        String oldAncestors = StringUtils.isNotNull(oldCategory) ? oldCategory.getAncestors() : "";
        productCategory.setAncestors(newAncestors);
        updateProductCategoryChildren(productCategory.getCategoryId(), newAncestors, oldAncestors);
        productCategory.setUpdateTime(DateUtils.getNowDate());
        return productCategoryMapper.updateProductCategory(productCategory);
    }

    /**
     * 批量删除商品类别
     * 
     * @param categoryIds 需要删除的商品类别主键
     * @return 结果
     */
    @Override
    public int deleteProductCategoryByCategoryIds(Long[] categoryIds)
    {
        return productCategoryMapper.deleteProductCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除商品类别信息
     * 
     * @param categoryId 商品类别主键
     * @return 结果
     */
    @Override
    public int deleteProductCategoryByCategoryId(Long categoryId)
    {
        return productCategoryMapper.deleteProductCategoryByCategoryId(categoryId);
    }

    @Override
    public boolean checkCategoryNameUnique(ProductCategory productCategory)
    {
        Long categoryId = StringUtils.isNull(productCategory.getCategoryId()) ? -1L : productCategory.getCategoryId();
        Long parentId = StringUtils.isNull(productCategory.getParentId()) ? 0L : productCategory.getParentId();
        ProductCategory info = productCategoryMapper.checkProductCategoryNameUnique(productCategory.getCategoryName(), parentId);
        if (StringUtils.isNotNull(info) && info.getCategoryId().longValue() != categoryId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public List<ProductCategory> buildCategoryTree(List<ProductCategory> categories)
    {
        List<ProductCategory> returnList = new ArrayList<>();
        List<Long> ids = categories.stream().map(ProductCategory::getCategoryId).collect(Collectors.toList());
        for (ProductCategory item : categories)
        {
            if (!ids.contains(item.getParentId()))
            {
                recursionFn(categories, item);
                returnList.add(item);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = categories;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildCategoryTreeSelect(List<ProductCategory> categories)
    {
        List<ProductCategory> categoryTrees = buildCategoryTree(categories);
        return categoryTrees.stream().map(this::convertTree).collect(Collectors.toList());
    }

    @Override
    public boolean hasChildByCategoryId(Long categoryId)
    {
        return productCategoryMapper.hasChildByCategoryId(categoryId) > 0;
    }

    /**
     * 递归列表 -> 树
     */
    private void recursionFn(List<ProductCategory> list, ProductCategory parent)
    {
        List<ProductCategory> childList = getChildren(list, parent);
        parent.setChildren(childList);
        for (ProductCategory child : childList)
        {
            if (hasChild(list, child))
            {
                recursionFn(list, child);
            }
        }
    }

    private List<ProductCategory> getChildren(List<ProductCategory> list, ProductCategory parent)
    {
        return list.stream()
                .filter(item -> StringUtils.isNotNull(item.getParentId()) && item.getParentId().longValue() == parent.getCategoryId().longValue())
                .collect(Collectors.toList());
    }

    private boolean hasChild(List<ProductCategory> list, ProductCategory parent)
    {
        return !getChildren(list, parent).isEmpty();
    }

    private TreeSelect convertTree(ProductCategory category)
    {
        TreeSelect node = new TreeSelect();
        node.setId(category.getCategoryId());
        node.setLabel(category.getCategoryName());
        if (CollectionUtils.isNotEmpty(category.getChildren()))
        {
            node.setChildren(category.getChildren().stream()
                    .filter(obj -> obj instanceof ProductCategory)
                    .map(obj -> convertTree((ProductCategory) obj))
                    .collect(Collectors.toList()));
        }
        return node;
    }

    /**
     * 修改子元素关系
     *
     * @param categoryId   当前节点
     * @param newAncestors 新祖级
     * @param oldAncestors 旧祖级
     */
    private void updateProductCategoryChildren(Long categoryId, String newAncestors, String oldAncestors)
    {
        List<ProductCategory> children = productCategoryMapper.selectChildrenProductCategoryById(categoryId);
        if (CollectionUtils.isEmpty(children))
        {
            return;
        }
        for (ProductCategory child : children)
        {
            if (StringUtils.isNotEmpty(oldAncestors))
            {
                child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            }
            else
            {
                child.setAncestors(newAncestors);
            }
        }
        productCategoryMapper.updateProductCategoryChildren(children);
    }
}
