import dayjs from 'dayjs'

// 格式化日期时间
export const formatDateTime = (date: string | Date, format: string = 'YYYY-MM-DD HH:mm:ss'): string => {
  return dayjs(date).format(format)
}

// 格式化日期
export const formatDate = (date: string | Date): string => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 格式化时间
export const formatTime = (date: string | Date): string => {
  return dayjs(date).format('HH:mm:ss')
}

// 相对时间
export const fromNow = (date: string | Date): string => {
  return dayjs(date).fromNow()
}

// 判断是否为今天
export const isToday = (date: string | Date): boolean => {
  return dayjs(date).isSame(dayjs(), 'day')
}

// 判断是否为昨天
export const isYesterday = (date: string | Date): boolean => {
  return dayjs(date).isSame(dayjs().subtract(1, 'day'), 'day')
}

// 获取时间差
export const getTimeDiff = (startDate: string | Date, endDate: string | Date): number => {
  return dayjs(endDate).diff(dayjs(startDate), 'millisecond')
}

// 格式化文件大小
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化数字
export const formatNumber = (num: number): string => {
  if (num < 1000) return num.toString()
  if (num < 1000000) return (num / 1000).toFixed(1) + 'K'
  if (num < 1000000000) return (num / 1000000).toFixed(1) + 'M'
  return (num / 1000000000).toFixed(1) + 'B'
}

// 格式化百分比
export const formatPercent = (num: number, decimals: number = 2): string => {
  return (num * 100).toFixed(decimals) + '%'
}

// 截断文本
export const truncateText = (text: string, length: number): string => {
  if (text.length <= length) return text
  return text.substring(0, length) + '...'
}

// 防抖函数
export const debounce = <T extends (...args: any[]) => any>(
  func: T,
  delay: number
): ((...args: Parameters<T>) => void) => {
  let timeoutId: ReturnType<typeof setTimeout>
  
  return (...args: Parameters<T>) => {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => func(...args), delay)
  }
}

// 节流函数
export const throttle = <T extends (...args: any[]) => any>(
  func: T,
  delay: number
): ((...args: Parameters<T>) => void) => {
  let lastExecTime = 0
  
  return (...args: Parameters<T>) => {
    const currentTime = Date.now()
    if (currentTime - lastExecTime >= delay) {
      func(...args)
      lastExecTime = currentTime
    }
  }
}

// 深拷贝
export const deepClone = <T>(obj: T): T => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime()) as any
  if (obj instanceof Array) return obj.map(item => deepClone(item)) as any
  if (obj instanceof Object) {
    const clonedObj = {} as any
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
  return obj
}

// 生成随机ID
export const generateId = (): string => {
  return Math.random().toString(36).substr(2, 9)
}

// 验证邮箱
export const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 验证手机号
export const validatePhone = (phone: string): boolean => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

// 验证密码强度
export const validatePassword = (password: string): {
  valid: boolean
  strength: 'weak' | 'medium' | 'strong'
  message: string
} => {
  if (password.length < 6) {
    return { valid: false, strength: 'weak', message: '密码至少6位' }
  }
  
  let strength = 0
  if (password.length >= 8) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/\d/.test(password)) strength++
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength++
  
  if (strength < 2) {
    return { valid: false, strength: 'weak', message: '密码强度过弱' }
  } else if (strength < 4) {
    return { valid: true, strength: 'medium', message: '密码强度中等' }
  } else {
    return { valid: true, strength: 'strong', message: '密码强度很强' }
  }
}

// 获取文件扩展名
export const getFileExtension = (filename: string): string => {
  return filename.split('.').pop() || ''
}

// 下载文件
export const downloadFile = (url: string, filename: string): void => {
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 复制到剪贴板
export const copyToClipboard = async (text: string): Promise<boolean> => {
  try {
    await navigator.clipboard.writeText(text)
    return true
  } catch (error) {
    // 降级方案
    const textArea = document.createElement('textarea')
    textArea.value = text
    document.body.appendChild(textArea)
    textArea.focus()
    textArea.select()
    
    try {
      document.execCommand('copy')
      return true
    } catch (fallbackError) {
      return false
    } finally {
      document.body.removeChild(textArea)
    }
  }
}

// 获取设备类型
export const getDeviceType = (): 'desktop' | 'mobile' => {
  const userAgent = navigator.userAgent.toLowerCase()
  const mobile = /android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i.test(userAgent)
  return mobile ? 'mobile' : 'desktop'
}

// 获取浏览器信息
export const getBrowserInfo = (): {
  name: string
  version: string
} => {
  const userAgent = navigator.userAgent
  let browserName = 'Unknown'
  let browserVersion = 'Unknown'
  
  if (userAgent.indexOf('Chrome') > -1) {
    browserName = 'Chrome'
    browserVersion = userAgent.match(/Chrome\/(\d+)/)?.[1] || 'Unknown'
  } else if (userAgent.indexOf('Firefox') > -1) {
    browserName = 'Firefox'
    browserVersion = userAgent.match(/Firefox\/(\d+)/)?.[1] || 'Unknown'
  } else if (userAgent.indexOf('Safari') > -1) {
    browserName = 'Safari'
    browserVersion = userAgent.match(/Version\/(\d+)/)?.[1] || 'Unknown'
  } else if (userAgent.indexOf('Edge') > -1) {
    browserName = 'Edge'
    browserVersion = userAgent.match(/Edge\/(\d+)/)?.[1] || 'Unknown'
  }
  
  return { name: browserName, version: browserVersion }
}