import SockJS from "sockjs-client";
import * as Stomp from "stompjs";

class webSocket {
    //构造函数
    constructor() {
        this.tryTimes = 1; // 重连次数
        this.callback = null; // 回调函数
        this.stompClient = null; // stomp对象
        this.reconTimeout = null; //重连延时器
        this.sendTimeout = null; // 重发心跳延时器 - 10s发一次
        this.vm = null;
    }

    /** socket连接 */
    connectionSocket() {
        //连接SockJS
        let socket = new SockJS(`${this.getSocketUrl()}`);

        // 获取STOMP子协议的客户端对象
        this.stompClient = Stomp.over(socket);

        //日志不打印
        this.stompClient.debug = () => {};

        // 向服务器发起websocket连接
        this.stompClient.connect({
            userId: '自己订阅送的id'
        }, () => {
            //tryTimes定义重置
            this.tryTimes = 1;
            //订阅消息
            this.subscribeToServer();
            //心跳单独通道
            this.stompClient.subscribe(`/server/net/${'自己订阅送的id'}`, (response) => {
                if (response && response.body && JSON.parse(response.body).code == '0') {
                    //再次进行心跳发送
                    this.heartCheck();
                }
            });
        }, (err) => {
            // 连接发生错误时的处理函数
            console.log('失败');
            if (this.vm.$message) this.vm.$message({
                message: '连接发生错误，请刷新网页再次连接！',
                type: 'warning'
            });
        });
    }
    /** 订阅服务端 */
    subscribeToServer() {
        // 订阅服务端提供的某个topic
        this.stompClient.subscribe(`/ws-genius`, (response) => {
            if (response) {
                if (this.callback) this.callback(response);
            }
        });
    }

    /** 赋值、初始化socket */
    initWebSocket(back, me) {
        this.vm = me;
        //列表赋值
        this.callback = back;
        //初始化连接
        this.connectionSocket();
        //加载心跳
        this.heartCheck();
    }

    /** 重连 */
    reconnect() {
        this.destroy();
        if (this.tryTimes > 10) {
            if (this.vm.$message) this.vm.$message({
                message: '重连次数已达上限，连接失败。请刷新网页再次连接！',
                type: 'warning'
            });
            return;
        }
        this.tryTimes++;
        //再次连接
        this.connectionSocket();
        this.heartCheck();
    }

    /** 心跳检测机制 */
    heartCheck() {
        //先清除重连机制
        if (this.reconTimeout) clearTimeout(this.reconTimeout);
        let me = this;
        me.sendTimeout = setTimeout(() => {
            me.sendSocket({
                to: `/server/net/${'自己订阅送的id'}`
            });
            //重连机制，十秒不被清除代表已经断开，进行重连
            me.reconTimeout = setTimeout(() => {
                me.reconnect();
            }, 10000);
        }, 10000);
    }

    /** 发送消息 */
    sendSocket(params) {
        try {
            this.stompClient.send("/client/path/points", {}, JSON.stringify(params));
        } catch (error) {
            console.log("发生异常了-------", error);
        }
    }

    /** 销毁 */
    destroy() {
        // 断开连接,清除定时器
        if (this.stompClient) this.stompClient.disconnect();
        if (this.reconTimeout) clearTimeout(this.reconTimeout);
        if (this.sendTimeout) clearTimeout(this.sendTimeout);
    }

    /** 获取动态socket地址，http环境下使用http，https环境下使用https访问websocket */
    getSocketUrl() {
        let wsUrl =
            window.location.origin +
            "/ws-genius?uuId=fa49201d414e4e2bb93f932a4631b723";
        return wsUrl;
    }
}
const WebSocket = new webSocket();
export default WebSocket;