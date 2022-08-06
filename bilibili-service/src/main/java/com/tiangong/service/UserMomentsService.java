package com.tiangong.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tiangong.dao.UserMomentsDao;
import com.tiangong.domain.UserMoment;
import com.tiangong.domain.constant.UserMomentsConstant;
import com.tiangong.util.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-19  11:21
 * @Description: 用户动态业务层
 * @Version: 1.0
 */
@Service
public class UserMomentsService {

    @Autowired
    private UserMomentsDao userMomentsDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
    * @description: 添加用户动态业务层
    * @author: ChenLipeng 
    * @date: 2022/6/19 11:50 
    * @param: userMoment
    **/
    public void addUserMoments(UserMoment userMoment) throws Exception {
        //设置动态创建时间
        userMoment.setCreateTime(new Date());
        //调用数据层添加用户动态
        userMomentsDao.addUserMoments(userMoment);
        //从上下文中获取生产者的bean
        DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean("momentsProducer");
        //创建消息
        //在消息中置入topic
        //在消息中再置入动态的byte数组
        Message msg = new Message(UserMomentsConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        //同步发送消息
        RocketMQUtil.syncSendMsg(producer, msg);
    }

    /**
    * @description: 获取用户发布的动态
    * @author: ChenLipeng
    * @date: 2022/7/15 18:55
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.UserMoment>
    **/
    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        //设置查询关键字
        String key = "subscribed-" + userId;
        //根据关键字获取动态
        String listStr = redisTemplate.opsForValue().get(key);
        //返回结果
        return JSONArray.parseArray(listStr, UserMoment.class);
    }
}
