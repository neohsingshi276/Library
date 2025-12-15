package com.secondhand.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.secondhand.common.annotation.Log;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.domain.TreeSelect;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.system.domain.ProductCategory;
import com.secondhand.system.service.IProductCategoryService;
import com.secondhand.common.utils.poi.ExcelUtil;
import com.secondhand.common.constant.UserConstants;

/**
 * 商品类别Controller
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
@RestController
@RequestMapping("/system/product_category")
public class ProductCategoryController extends BaseController
{
    @Autowired
    private IProductCategoryService productCategoryService;

    /**
     * 查询商品类别列表
     */
    @PreAuthorize("@ss.hasPermi('system:product_category:list')")
    @GetMapping("/list")
    public AjaxResult list(ProductCategory productCategory)
    {
        List<ProductCategory> list = productCategoryService.selectProductCategoryList(productCategory);
        return success(list);
    }

    /**
     * 下拉树结构
     */
    @PreAuthorize("@ss.hasPermi('system:product_category:list')")
    @GetMapping("/treeselect")
    public AjaxResult treeselect(ProductCategory productCategory)
    {
        List<ProductCategory> categories = productCategoryService.selectProductCategoryList(productCategory);
        List<TreeSelect> tree = productCategoryService.buildCategoryTreeSelect(categories);
        return success(tree);
    }

    /**
     * 导出商品类别列表
     */
    @PreAuthorize("@ss.hasPermi('system:product_category:export')")
    @Log(title = "商品类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProductCategory productCategory)
    {
        List<ProductCategory> list = productCategoryService.selectProductCategoryList(productCategory);
        ExcelUtil<ProductCategory> util = new ExcelUtil<ProductCategory>(ProductCategory.class);
        util.exportExcel(response, list, "商品类别数据");
    }

    /**
     * 获取商品类别详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:product_category:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(productCategoryService.selectProductCategoryByCategoryId(categoryId));
    }

    /**
     * 新增商品类别
     */
    @PreAuthorize("@ss.hasPermi('system:product_category:add')")
    @Log(title = "商品类别", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProductCategory productCategory)
    {
        if (!productCategoryService.checkCategoryNameUnique(productCategory))
        {
            return error("新增商品类别'" + productCategory.getCategoryName() + "'失败，名称已存在");
        }
        productCategory.setCreateBy(getUsername());
        return toAjax(productCategoryService.insertProductCategory(productCategory));
    }

    /**
     * 修改商品类别
     */
    @PreAuthorize("@ss.hasPermi('system:product_category:edit')")
    @Log(title = "商品类别", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProductCategory productCategory)
    {
        if (productCategory.getCategoryId() != null && productCategory.getCategoryId().equals(productCategory.getParentId()))
        {
            return error("修改失败，父级不能选择自身");
        }
        if (!productCategoryService.checkCategoryNameUnique(productCategory))
        {
            return error("修改商品类别'" + productCategory.getCategoryName() + "'失败，名称已存在");
        }
        productCategory.setUpdateBy(getUsername());
        return toAjax(productCategoryService.updateProductCategory(productCategory));
    }

    /**
     * 删除商品类别
     */
    @PreAuthorize("@ss.hasPermi('system:product_category:remove')")
    @Log(title = "商品类别", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        for (Long categoryId : categoryIds)
        {
            if (productCategoryService.hasChildByCategoryId(categoryId))
            {
                return warn("存在子类别，无法直接删除");
            }
        }
        return toAjax(productCategoryService.deleteProductCategoryByCategoryIds(categoryIds));
    }
}
