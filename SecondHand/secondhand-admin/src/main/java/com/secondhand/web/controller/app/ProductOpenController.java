package com.secondhand.web.controller.app;

import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.page.TableDataInfo;
import com.secondhand.common.core.domain.TreeSelect;
import com.secondhand.system.domain.LLMImageRequest;
import com.secondhand.system.domain.LLMImageResponse;
import com.secondhand.system.domain.Product;
import com.secondhand.system.domain.ProductAiRequest;
import com.secondhand.system.domain.ProductCategory;
import com.secondhand.system.mapper.ProductCategoryMapper;
import com.secondhand.system.mapper.ProductCategoryMapper;
import com.secondhand.system.service.IProductService;
import com.secondhand.system.service.IProductCategoryService;
import com.secondhand.system.service.IProductFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户端开放商品接口（无权限拦截）
 */
@RestController
@RequestMapping("/app/product")
public class ProductOpenController extends BaseController
{
    @Autowired
    private IProductService productService;

    @Autowired
    private IProductCategoryService productCategoryService;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private IProductFavoriteService productFavoriteService;

    /**
     * AI识别 + 创建商品
     */
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
    @PostMapping("/ai/detect")
    public AjaxResult aiDetect(@RequestBody LLMImageRequest aiReq)
    {
        aiReq.setUserId(getUserId());
        LLMImageResponse resp = productService.aiDetect(aiReq);
        return success(resp);
    }

    /**
     * 查询当前用户的商品列表
     */
    @GetMapping("/my/list")
    public TableDataInfo myProductList(Product product)
    {
        // 设置当前用户ID为卖家ID，只查询当前用户的商品
        product.setSellerId(getUserId());
        startPage();
        List<Product> list = productService.selectProductList(product);
        return getDataTable(list);
    }

    /**
     * 查询当前用户已上架的商品列表（用于交换选择）
     */
    @GetMapping("/my/published")
    public TableDataInfo myPublishedProductList(Product product)
    {
        // 设置当前用户ID为卖家ID，只查询当前用户的商品
        product.setSellerId(getUserId());
        // 只查询已上架的商品
        product.setStatus("published");
        startPage();
        List<Product> list = productService.selectProductList(product);
        return getDataTable(list);
    }

    /**
     * 查询当前用户的商品详情
     */
    @GetMapping("/my/{productId}")
    public AjaxResult getMyProductInfo(@PathVariable Long productId)
    {
        Product product = productService.selectProductById(productId);
        // 验证是否为当前用户的商品
        if (product == null || !product.getSellerId().equals(getUserId()))
        {
            return error("商品不存在或无权限访问");
        }
        return success(product);
    }

    /**
     * 删除当前用户的商品
     */
    @DeleteMapping("/my/{productId}")
    public AjaxResult deleteMyProduct(@PathVariable Long productId)
    {
        Product product = productService.selectProductById(productId);
        // 验证是否为当前用户的商品
        if (product == null || !product.getSellerId().equals(getUserId()))
        {
            return error("商品不存在或无权限删除");
        }
        // 仅待审核或已拒绝允许删除（已上架、已下架、已售完不可删除）
        if (!"pending".equals(product.getStatus()) && !"rejected".equals(product.getStatus()))
        {
            return error("只有待审核或已拒绝的商品可以删除");
        }
        return toAjax(productService.deleteProductById(productId));
    }

    /**
     * 上架商品
     */
    @PutMapping("/my/{productId}/online")
    public AjaxResult onlineProduct(@PathVariable Long productId)
    {
        Product product = productService.selectProductById(productId);
        // 验证是否为当前用户的商品
        if (product == null || !product.getSellerId().equals(getUserId()))
        {
            return error("商品不存在或无权限操作");
        }
        return toAjax(productService.onlineProduct(productId));
    }

    /**
     * 下架商品
     */
    @PutMapping("/my/{productId}/offline")
    public AjaxResult offlineProduct(@PathVariable Long productId)
    {
        Product product = productService.selectProductById(productId);
        // 验证是否为当前用户的商品
        if (product == null || !product.getSellerId().equals(getUserId()))
        {
            return error("商品不存在或无权限操作");
        }
        return toAjax(productService.offlineProduct(productId));
    }

    /**
     * 查询已上架商品列表（公开接口，无需登录）
     */
    @GetMapping("/list")
    public TableDataInfo listPublishedProducts(
            Product product,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String order)
    {
        // 只查询已上架的商品
        product.setStatus("published");

        // 设置分类ID（优先使用明确传递的categoryId参数）
        if (categoryId != null) {
            product.setCategoryId(categoryId);
        }

        // 设置价格筛选
        if (minPrice != null) {
            product.setMinPrice(minPrice);
        }
        if (maxPrice != null) {
            product.setMaxPrice(maxPrice);
        }
        // 设置排序
        if (orderBy != null && !orderBy.isEmpty()) {
            product.setOrderBy(orderBy);
        }
        if (order != null && !order.isEmpty()) {
            product.setOrder(order);
        }
        // 如果选择了分类，查询该分类及其所有子分类的商品
        if (product.getCategoryId() != null) {
            Long selectedCategoryId = product.getCategoryId();
            List<ProductCategory> children = productCategoryMapper.selectChildrenProductCategoryById(selectedCategoryId);
            // 构建分类ID数组：包含当前分类及其所有子分类
            Long[] categoryIds = new Long[children.size() + 1];
            categoryIds[0] = selectedCategoryId; // 包含当前分类
            for (int i = 0; i < children.size(); i++) {
                categoryIds[i + 1] = children.get(i).getCategoryId();
            }
            product.setCategoryIds(categoryIds);
            product.setCategoryId(null); // 清空categoryId，使用categoryIds查询
            System.out.println("分类筛选 - 选择的分类ID: " + selectedCategoryId +
                              ", 子分类数量: " + children.size() +
                              ", 分类IDs数组: " + java.util.Arrays.toString(categoryIds));
        } else {
            // 如果没有选择分类，确保categoryIds也为null
            product.setCategoryIds(null);
        }
        startPage();
        List<Product> list = productService.selectProductList(product);
        TableDataInfo dataTable = getDataTable(list);
        System.out.println("查询结果 - 列表数量: " + (list != null ? list.size() : 0) +
                          ", 总数: " + (dataTable != null ? dataTable.getTotal() : 0));
        return dataTable;
    }

    /**
     * 查询商品详情（公开接口，无需登录）
     * 每次查看详情时，浏览次数加一
     */
    @GetMapping("/{productId}")
    public AjaxResult getProductInfo(@PathVariable Long productId)
    {
        Product product = productService.selectProductById(productId);
        // 只允许查看已上架的商品
        if (product == null || !"published".equals(product.getStatus()))
        {
            return error("商品不存在或已下架");
        }
        // 浏览次数加一
        productService.incrementViewCount(productId);
        // 重新查询以获取更新后的浏览次数
        product = productService.selectProductById(productId);

        // 如果用户已登录，检查是否已收藏
        Long userId = getUserId();
        if (userId != null)
        {
            boolean isFavorite = productFavoriteService.isFavorite(userId, productId);
        }

        return success(product);
    }

    /**
     * 获取分类树结构（公开接口，无需登录）
     */
    @GetMapping("/category/treeselect")
    public AjaxResult getCategoryTreeSelect()
    {
        ProductCategory category = new ProductCategory();
        List<ProductCategory> categories = productCategoryService.selectProductCategoryList(category);
        List<TreeSelect> tree = productCategoryService.buildCategoryTreeSelect(categories);
        return success(tree);
    }

    /**
     * 切换收藏状态（收藏/取消收藏）
     * 需要登录
     */
    @PostMapping("/{productId}/favorite")
    public AjaxResult toggleFavorite(@PathVariable Long productId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }

        Product product = productService.selectProductById(productId);
        if (product == null || !"published".equals(product.getStatus()))
        {
            return error("商品不存在或已下架");
        }

        try
        {
            boolean isFavorite = productFavoriteService.toggleFavorite(userId, productId);
            // 重新查询商品以获取更新后的收藏数量
            product = productService.selectProductById(productId);
            // 返回当前收藏状态
            return AjaxResult.success(
                isFavorite ? "收藏成功" : "取消收藏成功",
                isFavorite
            );
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 查询用户是否已收藏商品
     * 需要登录
     */
    @GetMapping("/{productId}/favorite/status")
    public AjaxResult getFavoriteStatus(@PathVariable Long productId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return success(false);
        }

        boolean isFavorite = productFavoriteService.isFavorite(userId, productId);
        return success(isFavorite);
    }

    /**
     * 查询当前用户的收藏列表
     * 需要登录
     */
    @GetMapping("/favorite/list")
    public TableDataInfo getFavoriteList(
            Product product,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return getDataTable(null);
        }

        // 查询用户的收藏记录
        com.secondhand.system.domain.ProductFavorite favorite = new com.secondhand.system.domain.ProductFavorite();
        favorite.setUserId(userId);
        startPage();
        List<com.secondhand.system.domain.ProductFavorite> favoriteList = productFavoriteService.selectProductFavoriteList(favorite);

        // 转换为商品列表
        List<Product> productList = new java.util.ArrayList<>();
        for (com.secondhand.system.domain.ProductFavorite fav : favoriteList)
        {
            Product p = productService.selectProductById(fav.getProductId());
            if (p != null && "published".equals(p.getStatus()))
            {
                productList.add(p);
            }
        }

        // 应用筛选条件
        if (product.getTitle() != null && !product.getTitle().trim().isEmpty())
        {
            String title = product.getTitle().trim().toLowerCase();
            productList = productList.stream()
                .filter(p -> p.getTitle() != null && p.getTitle().toLowerCase().contains(title))
                .collect(java.util.stream.Collectors.toList());
        }

        if (categoryId != null)
        {
            productList = productList.stream()
                .filter(p -> categoryId.equals(p.getCategoryId()))
                .collect(java.util.stream.Collectors.toList());
        }

        if (minPrice != null)
        {
            productList = productList.stream()
                .filter(p -> p.getSalePrice() != null && p.getSalePrice().compareTo(minPrice) >= 0)
                .collect(java.util.stream.Collectors.toList());
        }

        if (maxPrice != null)
        {
            productList = productList.stream()
                .filter(p -> p.getSalePrice() != null && p.getSalePrice().compareTo(maxPrice) <= 0)
                .collect(java.util.stream.Collectors.toList());
        }

        // 构建分页结果
        // 手动分页（因为筛选是在内存中进行的）
        com.secondhand.common.core.page.PageDomain pageDomain = com.secondhand.common.core.page.TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() != null ? pageDomain.getPageNum() : 1;
        int pageSize = pageDomain.getPageSize() != null ? pageDomain.getPageSize() : 10;
        int total = productList.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<Product> pagedList;
        if (start < total)
        {
            pagedList = productList.subList(start, end);
        }
        else
        {
            pagedList = new java.util.ArrayList<>();
        }

        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setCode(com.secondhand.common.constant.HttpStatus.SUCCESS);
        dataTable.setMsg("查询成功");
        dataTable.setRows(pagedList);
        dataTable.setTotal((long) total);

        return dataTable;
    }
}

