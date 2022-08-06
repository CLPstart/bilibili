package com.tiangong.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.util
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-19  10:02
 * @Description: RocketMQ的工具类
 * @Version: 1.0
 */
public class RocketMQUtil {

    /**
    * @description: 发送同步消息
    * @author: ChenLipeng 
    * @date: 2022/6/19 10:50
    * @param: producer 生产者
    * @param: msg 消息
    **/
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {
        SendResult result = producer.send(msg);
        System.out.println(result);
    }

    /**
    * @description: 发送异步消息
    * @author: ChenLipeng
    * @date: 2022/6/19 10:51
    * @param: producer
    * @param: msg
    **/
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {
        //设置消息发送次数
        int messageCount = 2;
        //消息发送倒计时
        CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        //遍历发送消息
        for (int i = 0; i < messageCount; i++) {
            //发送消息，并接收返回
            producer.send(msg, new SendCallback() { //消息返回结果的匿名内部类
                @Override
                public void onSuccess(SendResult sendResult) { //成功返回
                    //计数器减一
                    countDownLatch.countDown();
                    System.out.println(sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable throwable) {  //发生异常
                    //计数器减一
                    countDownLatch.countDown();
                    System.out.println("发送消息时发生了异常！" + throwable);
                    throwable.printStackTrace();
                }
            });
        }
        //等待
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

}
