package com.secondhand.system.service.impl;

import com.secondhand.system.mapper.ClientMapper;
import com.secondhand.system.mapper.ProductExchangeMapper;
import com.secondhand.system.mapper.ProductMapper;
import com.secondhand.system.mapper.ProductOrderMapper;
import com.secondhand.system.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 仪表盘统计Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class DashboardServiceImpl implements IDashboardService
{
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ProductExchangeMapper productExchangeMapper;

    @Override
    public Map<String, Object> getStatistics()
    {
        Map<String, Object> result = new HashMap<>();
        
        // 商品总数
        Long totalProducts = productMapper.countAllProducts();
        result.put("totalProducts", totalProducts != null ? totalProducts : 0);
        
        // 订单总数
        Long totalOrders = productOrderMapper.countAllOrders();
        result.put("totalOrders", totalOrders != null ? totalOrders : 0);
        
        // 用户总数
        Long totalUsers = clientMapper.countAllClients();
        result.put("totalUsers", totalUsers != null ? totalUsers : 0);
        
        // 交换总数
        Long totalExchanges = productExchangeMapper.countAllExchanges();
        result.put("totalExchanges", totalExchanges != null ? totalExchanges : 0);
        
        // 总交易金额
        BigDecimal totalAmount = productOrderMapper.sumOrderAmount();
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        // 今日新增商品
        Long todayProducts = productMapper.countTodayProducts();
        result.put("todayProducts", todayProducts != null ? todayProducts : 0);
        
        // 今日新增订单
        Long todayOrders = productOrderMapper.countTodayOrders();
        result.put("todayOrders", todayOrders != null ? todayOrders : 0);
        
        // 今日新增用户
        Long todayUsers = clientMapper.countTodayClients();
        result.put("todayUsers", todayUsers != null ? todayUsers : 0);
        
        return result;
    }

    @Override
    public Map<String, Object> getProductStatusStatistics()
    {
        Map<String, Object> result = new HashMap<>();
        
        // 查询各状态商品数量
        List<Map<String, Object>> statusCountList = productMapper.countProductsByStatus();
        
        List<Map<String, Object>> data = new ArrayList<>();
        long total = 0;
        
        // Status mapping
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("pending", "Pending Review");
        statusMap.put("published", "Published");
        statusMap.put("exchanging", "Exchanging");
        statusMap.put("exchanged", "Exchanged");
        statusMap.put("sold", "Sold");
        statusMap.put("offline", "Offline");
        statusMap.put("rejected", "Rejected");
        
        // 颜色映射
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("pending", "#E6A23C");
        colorMap.put("published", "#67C23A");
        colorMap.put("exchanging", "#409EFF");
        colorMap.put("exchanged", "#909399");
        colorMap.put("sold", "#606266");
        colorMap.put("offline", "#F56C6C");
        colorMap.put("rejected", "#F56C6C");
        
        for (Map<String, Object> item : statusCountList)
        {
            String status = (String) item.get("status");
            Long count = ((Number) item.get("count")).longValue();
            total += count;
            
            Map<String, Object> dataItem = new HashMap<>();
            dataItem.put("name", statusMap.getOrDefault(status, status));
            dataItem.put("value", count);
            Map<String, String> itemStyle = new HashMap<>();
            itemStyle.put("color", colorMap.getOrDefault(status, "#909399"));
            dataItem.put("itemStyle", itemStyle);
            data.add(dataItem);
        }
        
        result.put("data", data);
        result.put("total", total);
        
        return result;
    }

    @Override
    public Map<String, Object> getOrderTrendStatistics()
    {
        Map<String, Object> result = new HashMap<>();
        
        // 获取最近30天的订单趋势
        List<Map<String, Object>> trendData = productOrderMapper.getOrderTrendLast30Days();
        
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        
        // 生成最近30天的日期
        for (int i = 29; i >= 0; i--)
        {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            dates.add(sdf.format(calendar.getTime()));
        }
        
        // 创建日期到数据的映射
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        for (Map<String, Object> item : trendData)
        {
            String date = (String) item.get("date");
            dataMap.put(date, item);
        }
        
        // 填充数据
        for (String date : dates)
        {
            Map<String, Object> item = dataMap.get(date);
            if (item != null)
            {
                counts.add(((Number) item.get("count")).longValue());
                amounts.add((BigDecimal) item.getOrDefault("amount", BigDecimal.ZERO));
            }
            else
            {
                counts.add(0L);
                amounts.add(BigDecimal.ZERO);
            }
        }
        
        result.put("dates", dates);
        result.put("counts", counts);
        result.put("amounts", amounts);
        
        return result;
    }

    @Override
    public Map<String, Object> getTransactionAmountStatistics()
    {
        Map<String, Object> result = new HashMap<>();
        
        // 获取最近12个月的交易金额统计
        List<Map<String, Object>> monthlyData = productOrderMapper.getMonthlyTransactionAmount();
        
        List<String> months = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        
        // 生成最近12个月的月份
        for (int i = 11; i >= 0; i--)
        {
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, -i);
            months.add(sdf.format(calendar.getTime()));
        }
        
        // 创建月份到数据的映射
        Map<String, BigDecimal> dataMap = new HashMap<>();
        for (Map<String, Object> item : monthlyData)
        {
            String month = (String) item.get("month");
            BigDecimal amount = (BigDecimal) item.get("amount");
            dataMap.put(month, amount);
        }
        
        // 填充数据
        for (String month : months)
        {
            amounts.add(dataMap.getOrDefault(month, BigDecimal.ZERO));
        }
        
        result.put("months", months);
        result.put("amounts", amounts);
        
        return result;
    }

    @Override
    public Map<String, Object> getUserGrowthStatistics()
    {
        Map<String, Object> result = new HashMap<>();
        
        // 总用户数
        Long totalUsers = clientMapper.countAllClients();
        result.put("totalUsers", totalUsers != null ? totalUsers : 0);
        
        // 本月新增用户
        Long monthUsers = clientMapper.countThisMonthClients();
        result.put("monthUsers", monthUsers != null ? monthUsers : 0);
        
        // 上月新增用户
        Long lastMonthUsers = clientMapper.countLastMonthClients();
        result.put("lastMonthUsers", lastMonthUsers != null ? lastMonthUsers : 0);
        
        // 计算增长率
        double growthRate = 0.0;
        if (lastMonthUsers != null && lastMonthUsers > 0)
        {
            growthRate = ((double)(monthUsers != null ? monthUsers : 0) - lastMonthUsers) / lastMonthUsers * 100;
        }
        else if (monthUsers != null && monthUsers > 0)
        {
            growthRate = 100.0;
        }
        result.put("growthRate", String.format("%.2f", growthRate));
        
        // 获取最近7天的用户增长趋势
        List<Map<String, Object>> dailyData = clientMapper.getDailyUserGrowth();
        
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        
        // 生成最近7天的日期
        for (int i = 6; i >= 0; i--)
        {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            dates.add(sdf.format(calendar.getTime()));
        }
        
        // 创建日期到数据的映射
        Map<String, Long> dataMap = new HashMap<>();
        for (Map<String, Object> item : dailyData)
        {
            String date = (String) item.get("date");
            Long count = ((Number) item.get("count")).longValue();
            dataMap.put(date, count);
        }
        
        // 填充数据
        for (String date : dates)
        {
            counts.add(dataMap.getOrDefault(date, 0L));
        }
        
        result.put("dates", dates);
        result.put("counts", counts);
        
        return result;
    }
}

