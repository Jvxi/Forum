// 线型鼠标拖尾动画指令
const mouseTrail = {
  mounted(el) {
    // 检查是否已经存在拖尾元素
    if (document.querySelector('.mouse-trail-container')) {
      return
    }
    
    // 创建拖尾容器
    const trailContainer = document.createElement('div')
    trailContainer.className = 'mouse-trail-container'
    document.body.appendChild(trailContainer)
    
    // 拖尾点数组
    const trailPoints = []
    const maxPoints = 45 // 增加拖尾点数量
    
    // 初始化拖尾点
    for (let i = 0; i < maxPoints; i++) {
      const point = document.createElement('div')
      point.className = 'trail-point'
      // 根据位置设置不同的透明度和大小
      const opacity = Math.max(0.1, (maxPoints - i) / maxPoints * 0.9)
      const scale = 0.2 + (i / maxPoints) * 0.8
      point.style.opacity = opacity
      point.style.transform = `scale(${scale})`
      trailContainer.appendChild(point)
      trailPoints.push({
        element: point,
        x: 0,
        y: 0,
        targetX: 0,
        targetY: 0
      })
    }
    
    let mouseX = 0
    let mouseY = 0
    let isAnimating = false
    let lastMouseMoveTime = Date.now()
    let hideTimeout = null
    let isVisible = true
    
    // 鼠标移动事件
    const handleMouseMove = (e) => {
      mouseX = e.clientX
      mouseY = e.clientY
      lastMouseMoveTime = Date.now()
      
      // 如果拖尾是隐藏的，显示它
      if (!isVisible) {
        showTrail()
      }
      
      // 清除之前的隐藏定时器
      if (hideTimeout) {
        clearTimeout(hideTimeout)
      }
      
      // 设置新的隐藏定时器
      hideTimeout = setTimeout(() => {
        hideTrail()
      }, 3000) // 3秒后隐藏
      
      if (!isAnimating) {
        isAnimating = true
        animateTrail()
      }
    }
    
    // 显示拖尾
    const showTrail = () => {
      isVisible = true
      trailContainer.style.opacity = '1'
      trailContainer.style.pointerEvents = 'none'
    }
    
    // 隐藏拖尾
    const hideTrail = () => {
      isVisible = false
      trailContainer.style.opacity = '0'
      trailContainer.style.pointerEvents = 'none'
    }
    
    // 拖尾动画函数
    const animateTrail = () => {
      // 只有在可见时才更新位置
      if (isVisible) {
        // 更新第一个点的目标位置为鼠标位置
        trailPoints[0].targetX = mouseX
        trailPoints[0].targetY = mouseY
        
        // 更新每个点的位置
        for (let i = 0; i < trailPoints.length; i++) {
          const point = trailPoints[i]
          const prevPoint = i === 0 ? null : trailPoints[i - 1]
          
          if (i === 0) {
            // 第一个点直接跟随鼠标
            point.x = point.targetX
            point.y = point.targetY
          } else {
            // 其他点跟随前一个点，使用更高的插值系数
            point.targetX = prevPoint.x
            point.targetY = prevPoint.y
            
            // 大幅提高插值系数，让点与点之间跟随更紧密
            const interpolationFactor = 0.6 + (i / trailPoints.length) * 0.25
            
            const dx = point.targetX - point.x
            const dy = point.targetY - point.y
            
            point.x += dx * interpolationFactor
            point.y += dy * interpolationFactor
          }
          
          // 更新元素位置
          point.element.style.left = point.x + 'px'
          point.element.style.top = point.y + 'px'
        }
      }
      
      // 继续动画
      requestAnimationFrame(animateTrail)
    }
    
    // 为可交互元素添加悬停效果
    const handleMouseOver = (e) => {
      const target = e.target
      if (target.matches('button, a, .el-button, .el-card, .dev-card, .intro-card, .timeline-item, .highlight-feature, .highlight-tech, .highlight-ui, .highlight-category, .highlight-action, .highlight-code')) {
        trailContainer.classList.add('hover')
      }
    }
    
    const handleMouseOut = (e) => {
      const target = e.target
      if (target.matches('button, a, .el-button, .el-card, .dev-card, .intro-card, .timeline-item, .highlight-feature, .highlight-tech, .highlight-ui, .highlight-category, .highlight-action, .highlight-code')) {
        trailContainer.classList.remove('hover')
      }
    }
    
    // 添加事件监听器
    document.addEventListener('mousemove', handleMouseMove, { passive: true })
    document.addEventListener('mouseover', handleMouseOver)
    document.addEventListener('mouseout', handleMouseOut)
    
    // 清理函数
    const cleanup = () => {
      if (hideTimeout) {
        clearTimeout(hideTimeout)
      }
      if (trailContainer && trailContainer.parentNode) {
        trailContainer.parentNode.removeChild(trailContainer)
      }
      document.removeEventListener('mousemove', handleMouseMove)
      document.removeEventListener('mouseover', handleMouseOver)
      document.removeEventListener('mouseout', handleMouseOut)
    }
    
    // 将清理函数绑定到元素上
    el._mouseTrailCleanup = cleanup
  },
  
  unmounted(el) {
    if (el._mouseTrailCleanup) {
      el._mouseTrailCleanup()
    }
  }
}

// 导出为Vue指令
export default {
  install(app) {
    app.directive('mouse-trail', mouseTrail)
  }
}
