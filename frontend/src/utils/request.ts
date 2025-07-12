import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置NProgress
NProgress.configure({ showSpinner: false })

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 开始进度条
    NProgress.start()
    
    // 添加token
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers = {
        ...config.headers,
        Authorization: `Bearer ${userStore.token}`,
      }
    }
    
    return config
  },
  (error) => {
    // 结束进度条
    NProgress.done()
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 结束进度条
    NProgress.done()
    
    const { code, message, data } = response.data
    
    // 请求成功
    if (code === 200) {
      return data
    }
    
    // 处理业务错误
    if (code === 401) {
      // 未授权，清除token并跳转到登录页
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      ElMessage.error(message || '登录已过期，请重新登录')
      return Promise.reject(new Error(message || '登录已过期'))
    }
    
    if (code === 403) {
      ElMessage.error(message || '没有权限')
      return Promise.reject(new Error(message || '没有权限'))
    }
    
    // 其他错误
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    // 结束进度条
    NProgress.done()
    
    let message = '网络错误'
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          message = data.message || '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          message = '没有权限'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = data.message || `请求失败 (${status})`
      }
    } else if (error.request) {
      message = '网络连接失败'
    } else {
      message = error.message || '请求失败'
    }
    
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  }
)

// 封装请求方法
class ApiClient {
  // GET请求
  get<T = any>(url: string, params?: any): Promise<T> {
    return service.get(url, { params })
  }
  
  // POST请求
  post<T = any>(url: string, data?: any): Promise<T> {
    return service.post(url, data)
  }
  
  // PUT请求
  put<T = any>(url: string, data?: any): Promise<T> {
    return service.put(url, data)
  }
  
  // DELETE请求
  delete<T = any>(url: string, params?: any): Promise<T> {
    return service.delete(url, { params })
  }
  
  // 文件上传
  upload<T = any>(url: string, file: File, onProgress?: (progress: number) => void): Promise<T> {
    const formData = new FormData()
    formData.append('file', file)
    
    return service.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(progress)
        }
      },
    })
  }
  
  // 下载文件
  download(url: string, filename?: string): Promise<void> {
    return service.get(url, { responseType: 'blob' }).then((response) => {
      const blob = new Blob([response])
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = filename || 'download'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(downloadUrl)
    })
  }
}

export default new ApiClient()