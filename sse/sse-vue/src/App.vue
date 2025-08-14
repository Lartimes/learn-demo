<script setup>
import { ref, onUnmounted } from 'vue'

const loggedIn = ref(false)
const username = ref('')
const password = ref('')
const onlineUsers = ref([])
const notifications = ref([])
const isConnecting = ref(false)
const connectionStatus = ref('disconnected')
const currentUser = ref(null) // 保存当前用户信息
let eventSource = null
let heartbeatTimer = null
let reconnectTimer = null
let lastHeartbeat = null

function login() {
  if (!username.value.trim()) {
    alert('请输入用户名')
    return
  }
  
  if (!password.value.trim()) {
    alert('请输入密码')
    return
  }
  
  isConnecting.value = true
  
  fetch('http://localhost:8080/sse/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      name: username.value,
      password: password.value
    })
  })
  .then(response => {
    if (response.ok) {
      return response.json()
    } else {
      throw new Error('登录失败')
    }
  })
  .then(data => {
    console.log('登录成功:', data)
    
    if (data.flag && data.data) {
      // 保存用户信息
      currentUser.value = data.data
      loggedIn.value = true
      isConnecting.value = false
      
      // 使用uid建立SSE连接
      try {
        connectionStatus.value = 'connecting'
        const uid = currentUser.value.uid
        eventSource = new EventSource(`http://localhost:8080/sse/user/connection/${uid}`)
        
        eventSource.onopen = (event) => {
          console.log('SSE连接已建立')
          connectionStatus.value = 'connected'
          addNotification('连接成功', 'info')
          startHeartbeatCheck()
        }
        
        eventSource.onmessage = (event) => {
          console.log('收到SSE消息:', event.data)
          
          if (event.data === 'ping') {
            console.log('收到心跳消息')
            lastHeartbeat = Date.now()
            return
          }
          
          try {
            const data = JSON.parse(event.data)
            handleSSEMessage(data)
          } catch (error) {
            console.error('解析SSE消息失败:', error)
            addNotification('消息解析失败', 'error')
          }
        }
        
        eventSource.onerror = (event) => {
          console.error('SSE连接错误:', event)
          connectionStatus.value = 'error'
          addNotification('连接错误，尝试重连...', 'error')
          isConnecting.value = false
          stopHeartbeatCheck()
          scheduleReconnect()
        }
        
      } catch (error) {
        console.error('创建SSE连接失败:', error)
        connectionStatus.value = 'error'
        addNotification('连接失败', 'error')
        isConnecting.value = false
      }
    } else {
      throw new Error('登录失败')
    }
  })
  .catch(error => {
    console.error('登录失败:', error)
    alert('登录失败，请检查用户名和密码')
    isConnecting.value = false
  })
}

function handleSSEMessage(data) {
  console.log('处理SSE消息:', data)
  
  switch (data.type) {
    case 'online':
      if (!onlineUsers.value.includes(data.user)) {
        onlineUsers.value.push(data.user)
      }
      addNotification(`${data.user} 上线了`, 'online')
      break
      
    case 'offline':
      onlineUsers.value = onlineUsers.value.filter(u => u !== data.user)
      addNotification(`${data.user} 下线了`, 'offline')
      break
      
    case 'init':
      onlineUsers.value = data.users || []
      addNotification('获取在线用户列表', 'info')
      break
      
    case 'user_list':
      onlineUsers.value = data.users || []
      break
      
    case 'message':
      addNotification(data.message, 'message')
      break
      
    case 'system':
      addNotification(data.message, 'info')
      break
      
    case 'error':
      addNotification(data.message || '发生错误', 'error')
      break
      
    case 'notification':
      addNotification(data.message, data.notificationType || 'info')
      break
      
    default:
      if (data.message) {
        addNotification(data.message, 'info')
      } else if (data.type) {
        console.log('收到状态更新:', data)
      }
      break
  }
}

function addNotification(message, type = 'info') {
  const notification = {
    id: Date.now(),
    message,
    type,
    timestamp: new Date()
  }
  notifications.value.unshift(notification)
  
  if (notifications.value.length > 50) {
    notifications.value = notifications.value.slice(0, 50)
  }
}

function startHeartbeatCheck() {
  lastHeartbeat = Date.now()
  heartbeatTimer = setInterval(() => {
    const now = Date.now()
    if (lastHeartbeat && (now - lastHeartbeat) > 30000) {
      console.warn('心跳超时，连接可能断开')
      addNotification('连接超时，尝试重连...', 'error')
      connectionStatus.value = 'error'
      if (eventSource) {
        eventSource.close()
        eventSource = null
      }
      scheduleReconnect()
    }
  }, 10000)
}

function stopHeartbeatCheck() {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer)
    heartbeatTimer = null
  }
}

function scheduleReconnect() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
  }
  
  reconnectTimer = setTimeout(() => {
    if (loggedIn.value && connectionStatus.value === 'error' && currentUser.value) {
      console.log('尝试重新连接...')
      addNotification('正在重新连接...', 'info')
      
      // 重新建立SSE连接
      try {
        connectionStatus.value = 'connecting'
        const uid = currentUser.value.uid
        eventSource = new EventSource(`http://localhost:8080/sse/user/connection/${uid}`)
        
        eventSource.onopen = (event) => {
          console.log('SSE重连成功')
          connectionStatus.value = 'connected'
          addNotification('重连成功', 'info')
          startHeartbeatCheck()
        }
        
        eventSource.onmessage = (event) => {
          console.log('收到SSE消息:', event.data)
          
          if (event.data === 'ping') {
            console.log('收到心跳消息')
            lastHeartbeat = Date.now()
            return
          }
          
          try {
            const data = JSON.parse(event.data)
            handleSSEMessage(data)
          } catch (error) {
            console.error('解析SSE消息失败:', error)
            addNotification('消息解析失败', 'error')
          }
        }
        
        eventSource.onerror = (event) => {
          console.error('SSE重连错误:', event)
          connectionStatus.value = 'error'
          addNotification('重连失败', 'error')
          scheduleReconnect()
        }
        
      } catch (error) {
        console.error('创建SSE重连失败:', error)
        connectionStatus.value = 'error'
        addNotification('重连失败', 'error')
        scheduleReconnect()
      }
    }
  }, 3000)
}

function logout() {
  loggedIn.value = false
  username.value = ''
  password.value = ''
  currentUser.value = null
  onlineUsers.value = []
  notifications.value = []
  connectionStatus.value = 'disconnected'
  
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
  
  stopHeartbeatCheck()
  
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
}

function clearNotifications() {
  notifications.value = []
}

onUnmounted(() => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
  
  stopHeartbeatCheck()
  
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
})
</script>

<template>
  <div class="app-container">
    <!-- 头部 -->
    <header class="header">
      <div class="header-content">
        <h1 class="logo">🔗 实时通信系统</h1>
        <div class="user-section">
          <div v-if="!loggedIn" class="login-form">
            <input 
              v-model="username" 
              placeholder="请输入用户名" 
              class="username-input"
              @keyup.enter="login"
            />
            <input 
              v-model="password" 
              type="password"
              placeholder="请输入密码" 
              class="password-input"
              @keyup.enter="login"
            />
            <button 
              @click="login" 
              class="login-btn"
              :disabled="isConnecting"
            >
              <span v-if="isConnecting">连接中...</span>
              <span v-else>登录</span>
            </button>
          </div>
          <div v-else class="user-info">
            <span class="username">👤 {{ username }}</span>
            <div class="connection-status" :class="connectionStatus">
              <span class="status-dot"></span>
              <span class="status-text">
                {{ 
                  connectionStatus === 'connected' ? '已连接' :
                  connectionStatus === 'connecting' ? '连接中' :
                  connectionStatus === 'error' ? '连接错误' : '未连接'
                }}
              </span>
            </div>
            <button @click="logout" class="logout-btn">退出</button>
          </div>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="main-content">
      <div class="dashboard">
        <!-- 矩阵空间 -->
        <div class="matrix-section">
          <div class="section-header">
            <h2>🟢 在线用户矩阵</h2>
            <span class="user-count">{{ onlineUsers.length }} 人在线</span>
          </div>
          <div class="matrix-container">
            <div v-if="onlineUsers.length === 0" class="empty-state">
              <div class="empty-icon">👥</div>
              <p>暂无用户在线</p>
            </div>
            <div v-else class="user-grid">
              <div 
                v-for="user in onlineUsers" 
                :key="user"
                class="user-card"
              >
                <div class="user-avatar">{{ user.charAt(0) }}</div>
                <span class="user-name">{{ user }}</span>
                <div class="online-indicator"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 通知消息 -->
        <div class="notifications-section">
          <div class="section-header">
            <h2>📢 实时通知</h2>
            <button 
              @click="clearNotifications" 
              class="clear-btn"
              v-if="notifications.length > 0"
            >
              清空
            </button>
          </div>
          <div class="notifications-container">
            <div v-if="notifications.length === 0" class="empty-state">
              <div class="empty-icon">📭</div>
              <p>暂无通知消息</p>
            </div>
            <div v-else class="notifications-list">
              <div 
                v-for="notification in notifications" 
                :key="notification.id"
                class="notification-item"
                :class="notification.type"
              >
                <div class="notification-content">
                  <span class="notification-message">{{ notification.message }}</span>
                  <span class="notification-time">
                    {{ notification.timestamp.toLocaleTimeString() }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 1rem 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  color: white;
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0;
}

.user-section {
  display: flex;
  align-items: center;
}

.login-form {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  align-items: center;
}

.username-input, .password-input {
  padding: 0.75rem 1rem;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  font-size: 1rem;
  outline: none;
  transition: all 0.3s ease;
  min-width: 120px;
}

.username-input:focus, .password-input:focus {
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.3);
}

.login-btn, .logout-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  background: #4CAF50;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover:not(:disabled) {
  background: #45a049;
  transform: translateY(-2px);
}

.login-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.logout-btn {
  background: #f44336;
}

.logout-btn:hover {
  background: #da190b;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.username {
  color: white;
  font-weight: 600;
  font-size: 1.1rem;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
}

.connection-status.connected {
  background: rgba(76, 175, 80, 0.2);
  color: #4CAF50;
}

.connection-status.connecting {
  background: rgba(255, 152, 0, 0.2);
  color: #ff9800;
}

.connection-status.error {
  background: rgba(244, 67, 54, 0.2);
  color: #f44336;
}

.connection-status.disconnected {
  background: rgba(158, 158, 158, 0.2);
  color: #9e9e9e;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
  animation: pulse 2s infinite;
}

.connection-status.connected .status-dot {
  animation: none;
}

.connection-status.connecting .status-dot {
  animation: pulse 1s infinite;
}

.connection-status.error .status-dot {
  animation: pulse 0.5s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.status-text {
  font-size: 0.75rem;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.dashboard {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-top: 2rem;
}

.matrix-section, .notifications-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f0f0;
}

.section-header h2 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
  font-weight: 700;
}

.user-count {
  background: #4CAF50;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.clear-btn {
  background: #ff9800;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background 0.3s ease;
}

.clear-btn:hover {
  background: #f57c00;
}

.matrix-container, .notifications-container {
  min-height: 300px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #999;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-state p {
  margin: 0;
  font-size: 1.1rem;
}

.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 1rem;
}

.user-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 1rem;
  text-align: center;
  position: relative;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.user-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: #4CAF50;
}

.user-avatar {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0 auto 0.5rem;
}

.user-name {
  font-weight: 600;
  color: #333;
  font-size: 0.9rem;
}

.online-indicator {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  width: 12px;
  height: 12px;
  background: #4CAF50;
  border-radius: 50%;
  border: 2px solid white;
}

.notifications-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  padding: 1rem;
  margin-bottom: 0.5rem;
  border-radius: 8px;
  border-left: 4px solid #ddd;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.notification-item:hover {
  background: #e9ecef;
}

.notification-item.online {
  border-left-color: #4CAF50;
  background: #e8f5e8;
}

.notification-item.offline {
  border-left-color: #f44336;
  background: #ffebee;
}

.notification-item.message {
  border-left-color: #ff9800;
  background: #fff3e0;
}

.notification-item.error {
  border-left-color: #f44336;
  background: #ffebee;
}

.notification-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-message {
  font-weight: 500;
  color: #333;
}

.notification-time {
  font-size: 0.8rem;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard {
    grid-template-columns: 1fr;
  }
  
  .header-content {
    flex-direction: column;
    gap: 1rem;
  }
  
  .login-form {
    flex-direction: column;
    width: 100%;
    gap: 0.75rem;
  }
  
  .username-input, .password-input {
    width: 100%;
    min-width: auto;
  }
  
  .login-btn {
    width: 100%;
  }
  
  .user-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  }
  
  .main-content {
    padding: 1rem;
  }
}

/* 滚动条样式 */
.notifications-list::-webkit-scrollbar {
  width: 6px;
}

.notifications-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.notifications-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.notifications-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>