package com.secondhand.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.secondhand.common.annotation.Excel;
import com.secondhand.common.core.domain.TreeEntity;

/**
 * 商品类别对象 product_category
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
public class ProductCategory extends TreeEntity
{
    private static final long serialVersionUID = 1L;

    /** 类别ID */
    private Long categoryId;

    /** 类别名称 */
    @Excel(name = "类别名称")
    private String categoryName;

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("categoryId", getCategoryId())
            .append("parentId", getParentId())
            .append("ancestors", getAncestors())
            .append("categoryName", getCategoryName())
            .append("orderNum", getOrderNum())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
