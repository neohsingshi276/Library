package com.secondhand.web.controller.app;

import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.page.TableDataInfo;
import com.secondhand.system.domain.ProductOrderReview;
import com.secondhand.system.service.IProductOrderReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单评价Controller
 */
@RestController
@RequestMapping("/app/review")
public class ReviewController extends BaseController
{
    @Autowired
    private IProductOrderReviewService productOrderReviewService;

    /**
     * 创建评价
     */
    @PostMapping("/create")
    public AjaxResult createReview(@RequestBody Map<String, Object> params)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }

        if (!params.containsKey("orderId") || !params.containsKey("rating"))
        {
            return error("参数不完整");
        }

        Long orderId = Long.valueOf(params.get("orderId").toString());
        Integer rating = Integer.valueOf(params.get("rating").toString());
        String content = params.containsKey("content") && params.get("content") != null
            ? params.get("content").toString() : null;
        String reviewImages = params.containsKey("reviewImages") && params.get("reviewImages") != null
            ? params.get("reviewImages").toString() : null;

        try
        {
            ProductOrderReview review = productOrderReviewService.createReview(userId, orderId, rating, content, reviewImages);
            return success(review);
        }
        catch (RuntimeException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 查询评价详情
     */
    @GetMapping("/{reviewId}")
    public AjaxResult getInfo(@PathVariable Long reviewId)
    {
        ProductOrderReview review = productOrderReviewService.selectProductOrderReviewById(reviewId);
        if (review == null)
        {
            return error("评价不存在");
        }
        return success(review);
    }

    /**
     * 查询订单的所有评价
     */
    @GetMapping("/order/{orderId}")
    public AjaxResult getReviewsByOrderId(@PathVariable Long orderId)
    {
        List<ProductOrderReview> reviews = productOrderReviewService.selectProductOrderReviewListByOrderId(orderId);
        return success(reviews);
    }

    /**
     * 查询商品的所有评价
     */
    @GetMapping("/product/{productId}")
    public TableDataInfo getReviewsByProductId(@PathVariable Long productId)
    {
        startPage();
        List<Map<String, Object>> reviews = productOrderReviewService.selectProductOrderReviewListByProductId(productId);
        return getDataTable(reviews);
    }

    /**
     * 查询我的评价列表（作为评价人）
     */
    @GetMapping("/my-reviews")
    public TableDataInfo getMyReviews()
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return getDataTable(null);
        }

        startPage();
        List<Map<String, Object>> reviews = productOrderReviewService.selectProductOrderReviewListByReviewerId(userId);
        return getDataTable(reviews);
    }

    /**
     * 查询收到的评价列表（作为被评价人）
     */
    @GetMapping("/received-reviews")
    public TableDataInfo getReceivedReviews()
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return getDataTable(null);
        }

        startPage();
        List<Map<String, Object>> reviews = productOrderReviewService.selectProductOrderReviewListByReviewedId(userId);
        return getDataTable(reviews);
    }

    /**
     * 检查是否可以评价订单
     */
    @GetMapping("/check/{orderId}")
    public AjaxResult checkCanReview(@PathVariable Long orderId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }

        // 判断用户是买家还是卖家
        ProductOrderReview buyerReview = productOrderReviewService.selectProductOrderReviewByOrderAndReviewer(
            orderId, userId, "buyer");
        ProductOrderReview sellerReview = productOrderReviewService.selectProductOrderReviewByOrderAndReviewer(
            orderId, userId, "seller");

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("canReviewAsBuyer", buyerReview == null);
        result.put("canReviewAsSeller", sellerReview == null);
        result.put("hasBuyerReview", buyerReview != null);
        result.put("hasSellerReview", sellerReview != null);

        return success(result);
    }
}








