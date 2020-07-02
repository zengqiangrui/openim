package indi.qiangrui.openim.app.server;

import indi.qiangrui.openim.dto.msg.IMsg;
import indi.qiangrui.openim.dto.msg.TextMsg;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Johnny
 * @date 2020/7/2
 * @description todo 对视频流和群组消息的支撑，同时考虑从ServerContainer实现集群，结合redis中key策略实现分布式session
 * <p>
 * websocket 服务，暂时实现消息发送，对应直播场景
 * <p>
 * 消息的转发主要包含对象转发和输入流，对于输入流包括一对一和一对多的场景
 */
@ServerEndpoint("/websocket/{identifier}")
@Service
@Slf4j
public class WebSocketServer {

    private static int onlineCount = 0;
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    private Session session;
    private String identifier = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("identifier") String identifier) {
        this.session = session;
        this.identifier = identifier;
        if (!webSocketMap.containsKey(identifier)) {
            webSocketMap.put(identifier, this);
            addOnlineCount();

        }

        log.info("用户连接:" + identifier + ",当前在线数为:" + getOnlineCount());

        try {
            sendMessage(new TextMsg());
        } catch (IOException e) {
            log.error("用户:" + identifier + "网络IO异常");
        } catch (EncodeException e) {
            e.printStackTrace();
            log.error("发送消息转换异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(identifier)) {
            webSocketMap.remove(identifier);
            //从set中删除
            subOnlineCount();
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * <p>
     * 对于本socket 核心逻辑在于转发
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * 监听错误信息
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {

    }

    /**
     * 发送消息
     * <p>
     * todo 如果是视频场景，需要发送缓冲流
     */
    public void sendMessage(IMsg message) throws IOException, EncodeException {
        this.session.getBasicRemote().sendObject(message);
    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId)
        throws IOException {

    }

    /**
     * 获取在线情况 todo 优化synchronized处理
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}
