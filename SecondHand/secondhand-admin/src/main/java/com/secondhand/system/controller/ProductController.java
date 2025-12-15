package com.secondhand.system.controller;

import com.secondhand.common.annotation.Log;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.page.TableDataInfo;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.system.domain.LLMImageRequest;
import com.secondhand.system.domain.LLMImageResponse;
import com.secondhand.system.domain.Product;
import com.secondhand.system.domain.ProductAiRequest;
import com.secondhand.system.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品管理
 */
@RestController
@RequestMapping("/system/product")
public class ProductController extends BaseController
{
    @Autowired
    private IProductService productService;

    @PreAuthorize("@ss.hasPermi('system:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(Product product)
    {
        startPage();
        List<Product> list = productService.selectProductList(product);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:product:query')")
    @GetMapping("/{productId}")
    public AjaxResult getInfo(@PathVariable Long productId)
    {
        return success(productService.selectProductById(productId));
    }

    /**
     * AI识别 + 创建
     */
    @PreAuthorize("@ss.hasPermi('system:product:add')")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping("/ai")
    public AjaxResult aiCreate(@RequestBody ProductAiRequest request)
    {
        Product product = request.getProduct();
        LLMImageRequest aiReq = request.getAiRequest() != null ? request.getAiRequest() : new LLMImageRequest();
        aiReq.setUserId(request.getUserId() != null ? request.getUserId() : getUserId());
        Product created = productService.aiCreateProduct(product, aiReq);
        return success(created);
    }

    /**
     * 仅AI识别
     */
    @PreAuthorize("@ss.hasPermi('system:product:add')")
    @PostMapping("/ai/detect")
    public AjaxResult aiDetect(@RequestBody LLMImageRequest aiReq)
    {
        aiReq.setUserId(getUserId());
        LLMImageResponse resp = productService.aiDetect(aiReq);
        return success(resp);
    }

    @PreAuthorize("@ss.hasPermi('system:product:add')")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Product product)
    {
        product.setCreateBy(getUsername());
        return toAjax(productService.insertProduct(product));
    }

    @PreAuthorize("@ss.hasPermi('system:product:edit')")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Product product)
    {
        product.setUpdateBy(getUsername());
        return toAjax(productService.updateProduct(product));
    }

    @PreAuthorize("@ss.hasPermi('system:product:remove')")
    @Log(title = "商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(productService.deleteProductByIds(productIds));
    }

    /**
     * 审核商品（通过）
     */
    @PreAuthorize("@ss.hasPermi('system:product:audit')")
    @Log(title = "商品审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/pass/{productId}")
    public AjaxResult auditPass(@PathVariable Long productId, @RequestBody(required = false) Product product)
    {
        String auditRemark = product != null ? product.getAuditRemark() : null;
        BigDecimal donationRatio = product != null ? product.getDonationRatio() : null;
        return toAjax(productService.auditProductPass(productId, auditRemark, donationRatio));
    }

    /**
     * 审核商品（拒绝）
     */
    @PreAuthorize("@ss.hasPermi('system:product:audit')")
    @Log(title = "商品审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/reject/{productId}")
    public AjaxResult auditReject(@PathVariable Long productId, @RequestBody Product product)
    {
        if (product.getAuditRemark() == null || product.getAuditRemark().trim().isEmpty())
        {
            return error("审核拒绝必须填写拒绝原因");
        }
        return toAjax(productService.auditProductReject(productId, product.getAuditRemark()));
    }
}

