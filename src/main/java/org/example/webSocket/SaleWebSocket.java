package org.example.webSocket;

import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;

/**
 * 采购计划单 有变化后，给list页面发送消息
 * realWeight and realState
 */
@ServerEndpoint("/websocket/saleSocket")
@Component
public class SaleWebSocket extends BaseWebSocket {

    public void sendStringMessage(String message) {
        try {
            for (BaseWebSocket socket: webSockets) {
                // synchronized (session) {
                // sock.session.getBasicRemote().sendText(message);    //异步会报错，这里用同步好
                if (socket instanceof SaleWebSocket)
                    socket.session.getAsyncRemote().sendText(message);
                //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
