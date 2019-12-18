package com.dist.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * redis订阅方:接收消息
 *
 * 为了接收 Redis 渠道发送过来的消息，我们先定义一个消息监听器（ MessageListener ）
 * @author zhengja@dist.com.cn
 * @data 2019/6/11 13:58
 */
@Component
public class MyRedisSubscribeListener implements MessageListener {
    /**
     * 这里的 onMessage 方法是得到消息后的处理方法， 其中 message 参数代表 Redis 发送过来的消息，
     * pattern是渠道名称，onMessage方法里打印 了它们的内容。这里因为标注了 ＠Component 注解，所以
     * 在 Spring Boot 扫描后，会把它自动装配到 IoC 容器中 ,监听着对象RedisMessageListener会自动
     * 将消息进行转换。
     * @param message
     * @param bytes
     */
    @Override
    public void onMessage(Message message, byte[] bytes) {
        //消息体
        String body = null;
        try {
            //解决string乱码
            body = new String(message.getBody(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //渠道名称
        String topic = new String(bytes);
        System.out.println("消息体："+body);
        System.out.println("渠道名称："+topic);
    }
}
