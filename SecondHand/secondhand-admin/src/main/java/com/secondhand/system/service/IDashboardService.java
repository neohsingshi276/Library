package com.secondhand.system.service;

import java.util.Map;

/**
 * 仪表盘统计Service接口
 *
 * @author ruoyi
 */
public interface IDashboardService
{
    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据
     */
    Map<String, Object> getStatistics();

    /**
     * 获取商品状态统计（饼图数据）
     *
     * @return 商品状态统计数据
     */
    Map<String, Object> getProductStatusStatistics();

    /**
     * 获取订单趋势统计（折线图数据）
     *
     * @return 订单趋势统计数据
     */
    Map<String, Object> getOrderTrendStatistics();

    /**
     * 获取交易金额统计（柱状图数据）
     *
     * @return 交易金额统计数据
     */
    Map<String, Object> getTransactionAmountStatistics();

    /**
     * 获取用户增长统计（仪表盘数据）
     *
     * @return 用户增长统计数据
     */
    Map<String, Object> getUserGrowthStatistics();
}






