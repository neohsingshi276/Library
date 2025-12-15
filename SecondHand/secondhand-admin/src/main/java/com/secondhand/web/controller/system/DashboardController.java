package com.secondhand.web.controller.system;

import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.system.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 仪表盘统计Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dashboard")
public class DashboardController extends BaseController
{
    @Autowired
    private IDashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        Map<String, Object> statistics = dashboardService.getStatistics();
        return success(statistics);
    }

    /**
     * 获取商品状态统计（饼图数据）
     */
    @GetMapping("/product/status")
    public AjaxResult getProductStatusStatistics()
    {
        Map<String, Object> data = dashboardService.getProductStatusStatistics();
        return success(data);
    }

    /**
     * 获取订单趋势统计（折线图数据）
     */
    @GetMapping("/order/trend")
    public AjaxResult getOrderTrendStatistics()
    {
        Map<String, Object> data = dashboardService.getOrderTrendStatistics();
        return success(data);
    }

    /**
     * 获取交易金额统计（柱状图数据）
     */
    @GetMapping("/transaction/amount")
    public AjaxResult getTransactionAmountStatistics()
    {
        Map<String, Object> data = dashboardService.getTransactionAmountStatistics();
        return success(data);
    }

    /**
     * 获取用户增长统计（仪表盘数据）
     */
    @GetMapping("/user/growth")
    public AjaxResult getUserGrowthStatistics()
    {
        Map<String, Object> data = dashboardService.getUserGrowthStatistics();
        return success(data);
    }
}






