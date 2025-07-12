<template>
  <div ref="chartRef" class="chart"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

interface ChartData {
  name: string
  value: number
}

interface Props {
  data: ChartData[]
}

const props = defineProps<Props>()
const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

onMounted(() => {
  initChart()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})

watch(() => props.data, () => {
  updateChart()
}, { deep: true })

const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  
  const option = {
    title: {
      text: '数据趋势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: props.data.map(item => item.name),
      axisLabel: {
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '数量',
        type: 'bar',
        data: props.data.map((item, index) => ({
          value: item.value,
          itemStyle: {
            color: ['#409EFF', '#67C23A', '#E6A23C'][index] || '#909399'
          }
        })),
        barWidth: '60%',
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

const updateChart = () => {
  if (!chartInstance) return
  
  const option = {
    xAxis: {
      data: props.data.map(item => item.name)
    },
    series: [
      {
        data: props.data.map((item, index) => ({
          value: item.value,
          itemStyle: {
            color: ['#409EFF', '#67C23A', '#E6A23C'][index] || '#909399'
          }
        }))
      }
    ]
  }
  
  chartInstance.setOption(option)
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}
</script>

<style lang="scss" scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>