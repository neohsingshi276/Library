<template>
  <div class="dashboard-container">
    <!-- Statistics Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in statistics" :key="stat.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon :size="30">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-today" v-if="stat.today !== undefined">
                Today: {{ stat.today }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts Area -->
    <el-row :gutter="20" class="charts-row">
      <!-- Product Status Statistics - Pie Chart -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>Product Status Distribution</span>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- Order Trend Statistics - Line Chart -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>Order Trend (Last 30 Days)</span>
            </div>
          </template>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- Transaction Amount Statistics - Bar Chart -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>Transaction Amount Statistics (Last 12 Months)</span>
            </div>
          </template>
          <div ref="barChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 用户增长统计 - 仪表盘 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>User Growth Statistics</span>
            </div>
          </template>
          <div class="gauge-container">
            <div ref="gaugeChartRef" class="chart-container"></div>
            <div class="gauge-info">
              <div class="gauge-stat">
                <div class="gauge-label">Total Users</div>
                <div class="gauge-value">{{ userGrowth.totalUsers }}</div>
              </div>
              <div class="gauge-stat">
                <div class="gauge-label">This Month</div>
                <div class="gauge-value">{{ userGrowth.monthUsers }}</div>
              </div>
              <div class="gauge-stat">
                <div class="gauge-label">Growth Rate</div>
                <div class="gauge-value" :class="parseFloat(userGrowth.growthRate) >= 0 ? 'positive' : 'negative'">
                  {{ userGrowth.growthRate }}%
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { 
  getStatistics, 
  getProductStatusStatistics, 
  getOrderTrendStatistics, 
  getTransactionAmountStatistics, 
  getUserGrowthStatistics 
} from '@/api/system/dashboard'
import { 
  ShoppingBag, 
  ShoppingCart, 
  User, 
  Switch 
} from '@element-plus/icons-vue'

const statistics = ref([
  { title: 'Total Products', value: 0, today: 0, icon: 'ShoppingBag', color: '#409EFF' },
  { title: 'Total Orders', value: 0, today: 0, icon: 'ShoppingCart', color: '#67C23A' },
  { title: 'Total Users', value: 0, today: 0, icon: 'User', color: '#E6A23C' },
  { title: 'Total Exchanges', value: 0, today: 0, icon: 'Switch', color: '#F56C6C' }
])

const userGrowth = ref({
  totalUsers: 0,
  monthUsers: 0,
  growthRate: '0.00'
})

const pieChartRef = ref(null)
const lineChartRef = ref(null)
const barChartRef = ref(null)
const gaugeChartRef = ref(null)

let pieChart = null
let lineChart = null
let barChart = null
let gaugeChart = null

// 加载统计数据
async function loadStatistics() {
  try {
    const res = await getStatistics()
    if (res.code === 200) {
      const data = res.data
      statistics.value[0].value = data.totalProducts || 0
      statistics.value[0].today = data.todayProducts || 0
      statistics.value[1].value = data.totalOrders || 0
      statistics.value[1].today = data.todayOrders || 0
      statistics.value[2].value = data.totalUsers || 0
      statistics.value[2].today = data.todayUsers || 0
      statistics.value[3].value = data.totalExchanges || 0
    }
  } catch (error) {
    console.error('Failed to load statistics:', error)
  }
}

// 初始化饼图
function initPieChart() {
  if (!pieChartRef.value) return
  
  pieChart = echarts.init(pieChartRef.value, null, {
    width: 'auto',
    height: 'auto'
  })
  
  getProductStatusStatistics().then(res => {
    if (res.code === 200) {
      const data = res.data.data || []
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 'middle',
          textStyle: {
            fontSize: 12
          }
        },
        series: [
          {
            name: 'Product Status',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}\n{d}%'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              }
            },
            data: data
          }
        ]
      }
      
      pieChart.setOption(option)
    }
  })
}

// 初始化折线图
function initLineChart() {
  if (!lineChartRef.value) return
  
  lineChart = echarts.init(lineChartRef.value, null, {
    width: 'auto',
    height: 'auto'
  })
  
  getOrderTrendStatistics().then(res => {
    if (res.code === 200) {
      const data = res.data
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['Order Count', 'Transaction Amount']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: data.dates || []
        },
        yAxis: [
          {
            type: 'value',
            name: 'Order Count',
            position: 'left'
          },
          {
            type: 'value',
            name: 'Amount (¥)',
            position: 'right'
          }
        ],
        series: [
          {
            name: 'Order Count',
            type: 'line',
            smooth: true,
            data: data.counts || [],
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                  { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
                ]
              }
            }
          },
          {
            name: 'Transaction Amount',
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            data: (data.amounts || []).map(amt => amt ? amt.toFixed(2) : 0),
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      }
      
      lineChart.setOption(option)
    }
  })
}

// 初始化柱状图
function initBarChart() {
  if (!barChartRef.value) return
  
  barChart = echarts.init(barChartRef.value, null, {
    width: 'auto',
    height: 'auto'
  })
  
  getTransactionAmountStatistics().then(res => {
    if (res.code === 200) {
      const data = res.data
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            return params[0].name + '<br/>' + 
                   params[0].seriesName + ': ¥' + params[0].value
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: data.months || [],
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: 'Amount (¥)'
        },
        series: [
          {
            name: 'Transaction Amount',
            type: 'bar',
            data: (data.amounts || []).map(amt => amt ? amt.toFixed(2) : 0),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            },
            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#2378f7' },
                  { offset: 0.7, color: '#2378f7' },
                  { offset: 1, color: '#83bff6' }
                ])
              }
            }
          }
        ]
      }
      
      barChart.setOption(option)
    }
  })
}

// 初始化仪表盘
function initGaugeChart() {
  if (!gaugeChartRef.value) return
  
  gaugeChart = echarts.init(gaugeChartRef.value, null, {
    width: 'auto',
    height: 'auto'
  })
  
  getUserGrowthStatistics().then(res => {
    if (res.code === 200) {
      const data = res.data
      userGrowth.value = {
        totalUsers: data.totalUsers || 0,
        monthUsers: data.monthUsers || 0,
        growthRate: data.growthRate || '0.00'
      }
      
      // 计算增长率百分比（用于仪表盘显示）
      const growthRate = parseFloat(data.growthRate || 0)
      const gaugeValue = Math.min(Math.max(growthRate, -100), 100) // 限制在-100到100之间
      
      const option = {
        series: [
          {
            type: 'gauge',
            startAngle: 180,
            endAngle: 0,
            min: -100,
            max: 100,
            splitNumber: 8,
            axisLine: {
              lineStyle: {
                width: 6,
                color: [
                  [0.3, '#67e0e3'],
                  [0.7, '#37a2da'],
                  [1, '#fd666d']
                ]
              }
            },
            pointer: {
              icon: 'path://M12.8,0.7l12,40.1H0.7L12.8,0.7z',
              length: '12%',
              width: 20,
              offsetCenter: [0, '-60%'],
              itemStyle: {
                color: 'auto'
              }
            },
            axisTick: {
              length: 12,
              lineStyle: {
                color: 'auto',
                width: 2
              }
            },
            splitLine: {
              length: 20,
              lineStyle: {
                color: 'auto',
                width: 5
              }
            },
            axisLabel: {
              color: '#464646',
              fontSize: 12,
              distance: -60,
              rotate: 'tangential',
              formatter: function(value) {
                if (value === -100) return '↓-100%'
                if (value === 0) return '0%'
                if (value === 100) return '↑100%'
                return ''
              }
            },
            title: {
              offsetCenter: [0, '-20%'],
              fontSize: 20
            },
            detail: {
              fontSize: 30,
              offsetCenter: [0, '0%'],
              valueAnimation: true,
              formatter: function(value) {
                return value.toFixed(1) + '%'
              },
              color: 'auto'
            },
            data: [
              {
                value: gaugeValue,
                name: 'Growth Rate'
              }
            ]
          }
        ]
      }
      
      gaugeChart.setOption(option)
    }
  })
}

// 窗口大小改变时重新调整图表
function handleResize() {
  pieChart?.resize()
  lineChart?.resize()
  barChart?.resize()
  gaugeChart?.resize()
}

onMounted(() => {
  loadStatistics()
  
  nextTick(() => {
    initPieChart()
    initLineChart()
    initBarChart()
    initGaugeChart()
    
    window.addEventListener('resize', handleResize)
  })
})
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-today {
  font-size: 12px;
  color: #67C23A;
}

.charts-row {
  margin-top: 20px;
}

.chart-card {
  border-radius: 8px;
  margin-bottom: 20px;
  height: 500px;
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 100%;
  min-height: 400px;
  position: relative;
}

.chart-container > div {
  width: 100% !important;
  height: 100% !important;
}

.gauge-container {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.gauge-info {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  width: 100%;
}

.gauge-stat {
  text-align: center;
}

.gauge-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.gauge-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  
  &.positive {
    color: #67C23A;
  }
  
  &.negative {
    color: #F56C6C;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }
  
  .chart-card {
    height: 400px;
  }
  
  .chart-container {
    min-height: 300px;
  }
}
</style>
