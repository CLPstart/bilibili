package com.tiangong.service.websocket;

import com.alibaba.fastjson.JSONObject;
import com.tiangong.domain.Danmu;
import com.tiangong.domain.constant.UserMomentsConstant;
import com.tiangong.service.DanmuService;
import com.tiangong.util.RocketMQUtil;
import com.tiangong.util.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service.websocket
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-18  16:59
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@ServerEndpoint("/imserver/{token}")
public class WebsocketService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    public static final ConcurrentHashMap<String, WebsocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    public Session getSession() {
        return session;
    }

    private Session session;

    public String getSessionId() {
        return sessionId;
    }

    private String sessionId;

    private static ApplicationContext APPLOCATION_CONTEXT;

    private Long userId;

    /**
    * @description: 从主方法中获取springboot上下文参数以此实现多例模式注入
    * @author: ChenLipeng
    * @date: 2022/7/19 10:42
    * @param: applicationContext
    **/
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebsocketService.APPLOCATION_CONTEXT = applicationContext;
    }

    /**
    * @description: 建立websocket连接
    * @author: ChenLipeng
    * @date: 2022/7/19 10:42
    * @param: session
    * @param: token
    **/
    @OnOpen
    public void openConnection(Session session, @PathParam("token") String token){
        //根据token获取当前用户id
        try {
            this.userId = TokenUtil.verifyToken(token);
        } catch (Exception ignored) {}
        //对session信息赋值
        this.sessionId = session.getId();
        this.session = session;
        //更新MAP信息
        if (WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        }else{
            WEBSOCKET_MAP.put(sessionId, this);
            //在线人数加一
            ONLINE_COUNT.getAndIncrement();
        }
        //打印日志
        logger.info("用户连接成功：" + sessionId + "，当前在线人数：" + ONLINE_COUNT.get());
        //如果连接成功就发送信息否则就打印异常
        try {
            this.sendMessage("0");
        }catch (Exception e){
            logger.error("连接异常");
        }
    }

    /**
    * @description: 关闭连接
    * @author: ChenLipeng
    * @date: 2022/7/19 10:45
    **/
    @OnClose
    public void closeConnection(){
        //从MAP中删除连接
        if (WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        //打印日志
        logger.info("用户退出：" + sessionId + "，当前在线人数：" + ONLINE_COUNT.get());
    }

    /**
    * @description: 处理弹幕信息
    * @author: ChenLipeng
    * @date: 2022/7/19 10:46
    * @param: message
    **/
    @OnMessage
    public void onMessage(String message){
        //打印日志
        logger.info("用户信息：" + sessionId + "，报文：" + message);
        //message信息正常
        if (!StringUtil.isNullOrEmpty(message)){
            try{
                //群发消息
                for (Map.Entry<String, WebsocketService> entry : WEBSOCKET_MAP.entrySet()){
                    //遍历当前所有连接对象
                    WebsocketService websocketService = entry.getValue();
                    //将消息发送给生产者
                    DefaultMQProducer danmusProducer = (DefaultMQProducer) APPLOCATION_CONTEXT.getBean("danmusProducer");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", message);
                    jsonObject.put("sessionId", websocketService.getSessionId());
                    Message msg = new Message(UserMomentsConstant.TOPIC_DANMUS,
                            jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                    //异步发送消息
                    RocketMQUtil.asyncSendMsg(danmusProducer, msg);
                }
                if (this.userId != null){
                    //解析弹幕
                    Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                    //保存弹幕到数据库
                    danmu.setUserId(userId);
                    danmu.setCerateTime(new Date());
                    DanmuService danmuService = (DanmuService)APPLOCATION_CONTEXT.getBean("danmuService");
                    danmuService.asyncAddDanmu(danmu);
                    //保存弹幕到redis
                    danmuService.addDanmuToRedis(danmu);
                }
            }catch(Exception e){
                logger.error("弹幕接收出现异常！");
                e.printStackTrace();
            }
        }
    }

    /**
    * @description: 出错时执行的方法
    * @author: ChenLipeng
    * @date: 2022/7/19 10:51
    * @param: error
    **/
    @OnError
    public void onError(Throwable error){

    }

    /**
    * @description: 发送消息的方法
    * @author: ChenLipeng
    * @date: 2022/7/19 10:51
    * @param: message
    **/
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
    * @description: 定时任务，显示当前在线人数
    * @author: ChenLipeng
    * @date: 2022/7/19 15:36
    **/
    @Scheduled(fixedRate = 5000)
    private void noticeOnlineCount() throws IOException {
        for (Map.Entry<String, WebsocketService> entry : WebsocketService.WEBSOCKET_MAP.entrySet()){
            WebsocketService websocketService = entry.getValue();
            if (websocketService.session.isOpen()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", websocketService.userId);
                jsonObject.put("online_count", ONLINE_COUNT.get());
                jsonObject.put("msg", "当前在线人数为：" + ONLINE_COUNT.get());
                websocketService.sendMessage(jsonObject.toJSONString());
            }
        }
    }

}
