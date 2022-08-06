package com.tiangong.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.tiangong.domain.UserFollowing;
import com.tiangong.domain.UserMoment;
import com.tiangong.domain.constant.UserMomentsConstant;
import com.tiangong.service.UserFollowingService;
import com.tiangong.service.websocket.WebsocketService;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service.config
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-19  09:20
 * @Description: RocketMQ的配置类
 * @Version: 1.0
 */
@Configuration
public class RocketMQConfig {

    //RocketMQ的名称服务地址
    @Value("${rockermq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
    * @description: 动态的生产者
    * @author: ChenLipeng
    * @date: 2022/6/19 11:00
    * @return: org.apache.rocketmq.client.producer.DefaultMQProducer
    **/
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws Exception {
        //实例化动态生产者并进行分组
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        //设置MQ的名称服务器地址
        producer.setNamesrvAddr(nameServerAddr);
        //启动生产者服务
        producer.start();
        return producer;
    }

    /**
    * @description: 动态的消费者
    * @author: ChenLipeng
    * @date: 2022/6/19 11:01
    * @return: org.apache.rocketmq.client.consumer.DefaultMQPushConsumer
    **/
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws Exception{
        //实例化一个消费者并进行分组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        //设置名称服务器地址
        consumer.setNamesrvAddr(nameServerAddr);
        //订阅toic目录下的消息
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");
        //注册监听器
        consumer.registerMessageListener(new MessageListenerConcurrently(){  //异步监听的匿名内部类
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context){
                //获取当前第一个消息
                MessageExt msg = msgs.get(0);
                //判断一下消息是否为空
                if (msg == null){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                //得到消息体
                String bodyStr = new String(msg.getBody());
                //将消息体解析为java实体类
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);
                //从实体类中获取用户id
                Long userId = userMoment.getUserId();
                //根据用户id查询该用户的粉丝
                List<UserFollowing> fanList = userFollowingService.getUserFans(userId);
                //遍历所有粉丝
                for(UserFollowing fan : fanList){
                    //设置redis的key
                    String key = "subscribed-" + fan.getUserId();
                    //查询对应的value值是否为空
                    String subscribedListStr = redisTemplate.opsForValue().get(key);
                    //定义订阅列表
                    List<UserMoment> subscribedList;
                    if (StringUtil.isNullOrEmpty(subscribedListStr)){
                        subscribedList = new ArrayList<>();
                    }else {
                        //如果value值不为空则将value写入订阅列表中
                        subscribedList = JSONArray.parseArray(subscribedListStr, UserMoment.class);
                    }
                    //在订阅列表中添加新增加的动态
                    subscribedList.add(userMoment);
                    //将订阅列表重新写回redis
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

    /**
    * @description:
    * @author: ChenLipeng
    * @date: 2022/7/19 14:22
    * @return: org.apache.rocketmq.client.producer.DefaultMQProducer
    **/
    @Bean("danmusProducer")
    public DefaultMQProducer danmusProducer() throws Exception{
        //定义生产者以及组名称
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_DANMUS);
        //设置mq的名称服务器地址
        producer.setNamesrvAddr(nameServerAddr);
        //启动生产者服务
        producer.start();
        //返回生产者对象
        return producer;
    }

    /**
    * @description: 弹幕消费者
    * @author: ChenLipeng
    * @date: 2022/7/19 14:57
    * @return: org.apache.rocketmq.client.consumer.DefaultMQPushConsumer
    **/
    @Bean("danmusConsumer")
    public DefaultMQPushConsumer danmusConsumer() throws Exception{
        //实例化消费者对象，并设置分组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_DANMUS);
        //设置名称服务器
        consumer.setNamesrvAddr(nameServerAddr);
        //消费topic目录下的消息
        consumer.subscribe(UserMomentsConstant.TOPIC_DANMUS, "*");
        //注册监听器
        consumer.registerMessageListener(new MessageListenerConcurrently(){
            //消费消息
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                //消费第一条消息
                MessageExt msg = list.get(0);
                //获取消息体
                byte[] msgByte = msg.getBody();
                //把消息体转换为字符串
                String msgStr = new String(msgByte);
                //解析消息体字符串
                JSONObject jsonObject = JSONObject.parseObject(msgStr);
                //获取sessionId
                String sessionId = jsonObject.getString("sessionId");
                //获取消息
                String message = jsonObject.getString("message");
                //获取连接推送消息
                WebsocketService websocketService = WebsocketService.WEBSOCKET_MAP.get(sessionId);
                if (websocketService.getSession().isOpen()){
                    try{
                        websocketService.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //返回成功结果
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        //启动消费者
        consumer.start();
        return consumer;
    }

}
