package com.secondhand.system.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondhand.common.constant.UserConstants;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.exception.ServiceException;
import com.secondhand.common.utils.DateUtils;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.system.domain.LLMImageRequest;
import com.secondhand.system.domain.LLMImageResponse;
import com.secondhand.system.domain.Product;
import com.secondhand.system.domain.ProductCategory;
import com.secondhand.system.mapper.ProductCategoryMapper;
import com.secondhand.system.mapper.ProductMapper;
import com.secondhand.system.service.ILLMImageService;
import com.secondhand.system.service.IProductCategoryService;
import com.secondhand.system.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService
{
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private IProductCategoryService productCategoryService;

    @Autowired
    private ILLMImageService llmImageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Product selectProductById(Long productId)
    {
        return productMapper.selectProductById(productId);
    }

    @Override
    public List<Product> selectProductList(Product product)
    {
        return productMapper.selectProductList(product);
    }

    @Override
    public int insertProduct(Product product)
    {
        product.setCreateTime(DateUtils.getNowDate());
        product.setStatus(StringUtils.isEmpty(product.getStatus()) ? "pending" : product.getStatus());
        return productMapper.insertProduct(product);
    }

    @Override
    public int updateProduct(Product product)
    {
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }

    @Override
    public int deleteProductByIds(Long[] productIds)
    {
        return productMapper.deleteProductByIds(productIds);
    }

    @Override
    public int deleteProductById(Long productId)
    {
        return productMapper.deleteProductById(productId);
    }

    @Override
    public LLMImageResponse aiDetect(LLMImageRequest request)
    {
        return llmImageService.detectAndClassifyClothing(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product aiCreateProduct(Product product, LLMImageRequest request)
    {
        LLMImageResponse aiResp = llmImageService.detectAndClassifyClothing(request);
        if (aiResp.getSuccess() == null || !aiResp.getSuccess())
        {
            throw new ServiceException("AI recognition failed: " + aiResp.getErrorMessage());
        }
        if (Boolean.FALSE.equals(aiResp.getIsClothing()))
        {
            throw new ServiceException("Image not recognized as clothing, cannot publish");
        }

        // 尝试解析AI返回的 clothingDetails JSON
        String subCategory = null;
        String mainCategory = aiResp.getClothingCategory();
        String brand = null;
        String size = null;
        String condition = null;
        String description = null;
        try
        {
            if (StringUtils.isNotEmpty(aiResp.getClothingDetails()))
            {
                JsonNode node = objectMapper.readTree(aiResp.getClothingDetails());
                if (node.isObject()) {
                    subCategory = text(node, "subCategory");
                    brand = text(node, "brand");
                    size = text(node, "size");
                    condition = text(node, "condition");
                    description = text(node, "description");
                }
            }
        }
        catch (Exception e) {
            log.warn("Failed to parse AI returned JSON, will use text extraction: {}", e.getMessage());
        }

        // 以子类优先，没有则用主类
        String categoryName = StringUtils.isNotEmpty(subCategory) ? subCategory : mainCategory;
        String parentCategoryName = StringUtils.isNotEmpty(subCategory) ? mainCategory : null;
        
        // 如果仍然没有类别，尝试从AI原始响应中提取（英文类别）
        if (StringUtils.isEmpty(categoryName) && StringUtils.isNotEmpty(aiResp.getAiResponse()))
        {
            String aiText = aiResp.getAiResponse().toLowerCase();
            // 主类别（英文）
            String[] mainCategories = {"top", "bottom", "outerwear", "dress", "accessories", "shoes"};
            // 子类别及其对应的主类别（英文）
            String[][] subCategories = {
                {"t-shirt", "top"}, {"shirt", "top"}, {"sweatshirt", "top"}, {"sweater", "top"},
                {"pants", "bottom"}, {"skirt", "bottom"},
                {"jacket", "outerwear"}, {"coat", "outerwear"}, {"down jacket", "outerwear"},
                {"hat", "accessories"}
            };
            
            // 先查找子类别
            for (String[] subCat : subCategories)
            {
                if (aiText.contains(subCat[0].toLowerCase()))
                {
                    categoryName = capitalizeFirst(subCat[0]);
                    parentCategoryName = capitalizeFirst(subCat[1]);
                    log.info("Extracted subcategory from AI response text: {}, parent category: {}", categoryName, parentCategoryName);
                    break;
                }
            }
            
            // 如果没有找到子类别，查找主类别
            if (StringUtils.isEmpty(categoryName))
            {
                for (String cat : mainCategories)
                {
                    if (aiText.contains(cat.toLowerCase()))
                    {
                        categoryName = capitalizeFirst(cat);
                        log.info("Extracted main category from AI response text: {}", categoryName);
                        break;
                    }
                }
            }
        }
        
        // 如果还是没有，使用默认类别
        if (StringUtils.isEmpty(categoryName))
        {
            categoryName = "Other";
            log.warn("AI failed to identify specific category, using default category: {}", categoryName);
        }
        
        log.info("Final determined category name: {}, parent category: {}", categoryName, parentCategoryName);

        Long categoryId = ensureCategory(categoryName, parentCategoryName);
        product.setCategoryId(categoryId);
        
        // 设置卖家ID（必填字段）- 使用若依框架的SecurityUtils获取当前登录用户ID
        if (product.getSellerId() == null) {
            try {
                // 优先使用SecurityUtils获取当前登录用户ID（若依框架标准方式）
                product.setSellerId(SecurityUtils.getUserId());
            } catch (Exception e) {
                // 如果SecurityUtils获取失败（可能是未登录或匿名访问），尝试使用请求中的userId
                if (request.getUserId() != null) {
                    product.setSellerId(request.getUserId());
                } else {
                    throw new ServiceException("Unable to get user ID, cannot create product");
                }
            }
        }
        
        product.setAiCheckStatus("passed");
        product.setAiCheckResult(aiResp.getAiResponse());
        product.setAiCheckTime(new Date());
        // 用户端发布的商品默认为待审核状态，需要管理员审核后才能上架
        product.setStatus(StringUtils.isEmpty(product.getStatus()) ? "pending" : product.getStatus());

        // 填充可用的AI信息，用户传入优先
        if (StringUtils.isEmpty(product.getBrand()) && StringUtils.isNotEmpty(brand))
        {
            product.setBrand(brand);
        }
        if (StringUtils.isEmpty(product.getSize()) && StringUtils.isNotEmpty(size))
        {
            product.setSize(size);
        }
        if (StringUtils.isEmpty(product.getConditionLevel()) && StringUtils.isNotEmpty(condition))
        {
            product.setConditionLevel(condition);
        }
        if (StringUtils.isEmpty(product.getDescription()) && StringUtils.isNotEmpty(description))
        {
            product.setDescription(description);
        }

        if (product.getSalePrice() == null)
        {
            product.setSalePrice(BigDecimal.ZERO);
        }
        if (product.getDonationAmount() == null && product.getDonationRatio() != null && product.getSalePrice() != null)
        {
            product.setDonationAmount(product.getSalePrice().multiply(product.getDonationRatio()).divide(new BigDecimal("100")));
        }

        insertProduct(product);
        return product;
    }

    /**
     * 确保类别存在，支持层级查找和创建
     * @param categoryName 类别名称
     * @param parentCategoryName 父类别名称（可选）
     * @return 类别ID
     */
    private Long ensureCategory(String categoryName, String parentCategoryName)
    {
        // 第一步：先在所有层级中查找当前类别（避免重复创建）
        ProductCategory existCategory = findCategoryByNameAnywhere(categoryName);
        if (existCategory != null)
        {
            log.info("找到已存在的类别: {} (ID: {}, 父级ID: {}, 祖级: {})", 
                    categoryName, existCategory.getCategoryId(), existCategory.getParentId(), existCategory.getAncestors());
            return existCategory.getCategoryId();
        }
        
        // 第二步：如果不存在，且有父类别名称，查找或创建父类别
        Long parentId = 0L;
        String ancestors = "0";
        
        if (StringUtils.isNotEmpty(parentCategoryName))
        {
            // 在所有层级中查找父类别
            ProductCategory parentCategory = findCategoryByNameAnywhere(parentCategoryName);
            if (parentCategory == null)
            {
                // 创建父类别（作为根级）
                parentCategory = new ProductCategory();
                parentCategory.setCategoryName(parentCategoryName);
                parentCategory.setParentId(0L);
                parentCategory.setAncestors("0");
                parentCategory.setOrderNum(0);
                parentCategory.setCreateTime(DateUtils.getNowDate());
                productCategoryMapper.insertProductCategory(parentCategory);
                log.info("创建父类别: {}", parentCategoryName);
            }
            parentId = parentCategory.getCategoryId();
            ancestors = parentId.toString();
        }
        
        // 第三步：在指定父级下再次确认不存在（防止并发创建）
        ProductCategory checkAgain = productCategoryMapper.checkProductCategoryNameUnique(categoryName, parentId);
        if (checkAgain != null)
        {
            log.info("在父级{}下找到已存在的类别: {}", parentId, categoryName);
            return checkAgain.getCategoryId();
        }
        
        // 第四步：创建新类别
        ProductCategory add = new ProductCategory();
        add.setCategoryName(categoryName);
        add.setParentId(parentId);
        add.setAncestors(ancestors);
        add.setOrderNum(0);
        add.setCreateTime(DateUtils.getNowDate());
        productCategoryMapper.insertProductCategory(add);
        log.info("创建新类别: {} (父级ID: {}, 祖级: {})", categoryName, parentId, ancestors);
        return add.getCategoryId();
    }
    
    /**
     * 根据名称查找类别（在指定父级下查找）
     * @param categoryName 类别名称
     * @param parentId 父级ID（0表示查找根级）
     * @return 类别对象，如果不存在返回null
     */
    private ProductCategory findCategoryByName(String categoryName, Long parentId)
    {
        // 使用checkProductCategoryNameUnique方法，它会在指定父级下精确查找
        return productCategoryMapper.checkProductCategoryNameUnique(categoryName, parentId);
    }
    
    /**
     * 在所有层级中查找类别（用于查找父类别）
     * @param categoryName 类别名称
     * @return 类别对象，如果不存在返回null
     */
    private ProductCategory findCategoryByNameAnywhere(String categoryName)
    {
        ProductCategory query = new ProductCategory();
        query.setCategoryName(categoryName);
        List<ProductCategory> list = productCategoryMapper.selectProductCategoryList(query);
        if (list != null && !list.isEmpty()) {
            // 返回第一个匹配的（精确匹配优先）
            for (ProductCategory cat : list) {
                if (categoryName.equals(cat.getCategoryName())) {
                    return cat;
                }
            }
            // 如果没有精确匹配，返回第一个
            return list.get(0);
        }
        return null;
    }

    private String text(JsonNode node, String key)
    {
        return node != null && node.has(key) && !node.get(key).isNull() ? node.get(key).asText() : null;
    }

    /**
     * 将字符串首字母大写
     */
    private String capitalizeFirst(String str)
    {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditProductPass(Long productId, String auditRemark, BigDecimal donationRatio)
    {
        Product product = productMapper.selectProductById(productId);
        if (product == null)
        {
            throw new ServiceException("商品不存在");
        }
        
        // 计算捐赠金额
        BigDecimal donationAmount = BigDecimal.ZERO;
        if (donationRatio != null && donationRatio.compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal salePrice = product.getSalePrice();
            if (salePrice == null || salePrice.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("商品售价无效，无法计算捐赠金额");
            }
            
            // 根据捐赠比例计算捐赠金额
            donationAmount = salePrice.multiply(donationRatio).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            
            // 规则：捐赠金额不能超过50元
            BigDecimal maxDonationAmount = new BigDecimal("50");
            if (donationAmount.compareTo(maxDonationAmount) > 0)
            {
                donationAmount = maxDonationAmount;
            }
            
            // 如果捐赠金额小于0，设为0
            if (donationAmount.compareTo(BigDecimal.ZERO) < 0)
            {
                donationAmount = BigDecimal.ZERO;
            }
        }
        
        product.setStatus("published");
        product.setAuditRemark(auditRemark);
        product.setDonationRatio(donationRatio != null ? donationRatio : BigDecimal.ZERO);
        product.setDonationAmount(donationAmount);
        product.setAuditTime(DateUtils.getNowDate());
        product.setAuditBy(SecurityUtils.getUsername());
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditProductReject(Long productId, String auditRemark)
    {
        Product product = productMapper.selectProductById(productId);
        if (product == null)
        {
            throw new ServiceException("商品不存在");
        }
        if (StringUtils.isEmpty(auditRemark))
        {
            throw new ServiceException("审核拒绝必须填写拒绝原因");
        }
        product.setStatus("rejected");
        product.setAuditRemark(auditRemark);
        product.setAuditTime(DateUtils.getNowDate());
        product.setAuditBy(SecurityUtils.getUsername());
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int onlineProduct(Long productId)
    {
        Product product = productMapper.selectProductById(productId);
        if (product == null)
        {
            throw new ServiceException("商品不存在");
        }
        // 只有已下架的商品可以上架
        if (!"offline".equals(product.getStatus()))
        {
            throw new ServiceException("只有已下架的商品可以上架");
        }
        product.setStatus("published");
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int offlineProduct(Long productId)
    {
        Product product = productMapper.selectProductById(productId);
        if (product == null)
        {
            throw new ServiceException("商品不存在");
        }
        // 只有已上架的商品可以下架
        if (!"published".equals(product.getStatus()))
        {
            throw new ServiceException("只有已上架的商品可以下架");
        }
        product.setStatus("offline");
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int incrementViewCount(Long productId)
    {
        Product product = productMapper.selectProductById(productId);
        if (product == null)
        {
            return 0;
        }
        // 浏览次数加1，如果为null则初始化为1
        Integer currentViewCount = product.getViewCount();
        if (currentViewCount == null)
        {
            currentViewCount = 0;
        }
        product.setViewCount(currentViewCount + 1);
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }
}

