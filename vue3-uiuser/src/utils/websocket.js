import { ElNotification } from 'element-plus';
import { getToken } from "./auth";

let socket = null; // 实例对象
let lockReconnect = false; // 是否真正建立连接
const timeout = 20 * 1000; // 20秒一次心跳
let timeoutObj = null; // 心跳倒计时
let serverTimeoutObj = null; // 服务心跳倒计时
let timeoutnum = null; // 断开重连倒计时

// 初始化WebSocket连接
export function initWebSocket(userId) {
  if ("WebSocket" in window) {
    if (!userId) {
      console.log("未登录！websocket工具获取不到userId");
    } else {
      const wsUrl = 'ws://localhost:8080/websocket/' + userId;
      socket = new WebSocket(wsUrl);
      socket.onerror = webSocketOnError;
      socket.onmessage = webSocketOnMessage;
      socket.onclose = closeWebsocket;
      socket.onopen = openWebsocket;
    }
  } else {
    ElNotification({
      title: "错误",
      message: "您的浏览器不支持websocket，请更换Chrome或者Firefox",
      type: 'error'
    });
  }
}

// 建立连接后的回调
function openWebsocket(e) {
  start();
}

// 开启心跳检测
function start() {
  clearTimeout(timeoutObj);
  clearTimeout(serverTimeoutObj);
  timeoutObj = setTimeout(() => {
    if (socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify({type: 'HEARTBEAT'})); // 发送心跳包
    } else {
      reconnect();
    }
    serverTimeoutObj = setTimeout(() => {
      socket.close();
    }, timeout);
  }, timeout);
}

// 重新连接
function reconnect() {
  if (lockReconnect) return;
  lockReconnect = true;
  clearTimeout(timeoutnum);
  timeoutnum = setTimeout(() => {
    initWebSocket(); // 如果需要，可以在这里传递userId
    lockReconnect = false;
  }, 1000);
}

// 重置心跳检测
function reset() {
  clearTimeout(timeoutObj);
  clearTimeout(serverTimeoutObj);
  start();
}

// 发送消息
export function sendWebsocket(message) {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(message);
  }
}

// 连接错误
function webSocketOnError(e) {
  reconnect();
}

// 接收到服务器消息
export function webSocketOnMessage(e) {
  if (getToken()) {
    window.dispatchEvent(
      new CustomEvent("onmessageWS", {
        detail: {
          data: JSON.parse(e.data)
        },
      })
    );
  }
  reset();
}

// 关闭连接
function closeWebsocket(e) {
  reconnect();
}

// 手动关闭WebSocket连接
export function close() {
  if(socket) socket.close();
}

// 导出所需的方法
export default { initWebSocket, sendWebsocket, webSocketOnMessage, close };