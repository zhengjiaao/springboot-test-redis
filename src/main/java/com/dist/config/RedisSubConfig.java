package com.dist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.Resource;

/**
 * redis 发布-接收 配置
 * @author zhengja@dist.com.cn
 * @data 2019/6/11 14:01
 */
@Configuration
public class RedisSubConfig {
    /**
     * 这里 RedisTemplate 和 RedisConnectionFactory 对象都是 Spring Boot 自动创建的，所以这里只是
     * 把它们注入进来，只需要使用＠Autowired 注解即可 。然后定义了一个任务池 ，并设置了任务池大小
     * 为 2 0 ，这样它将可 以运行线程 ，井进行阻塞，等待 Redis 消息的传入。接着再定义了一个 Redis消息
     * 监听的容器 RedisMessageListenerContainer，并且往容器设置了 Redis 连接工厂和指定运行消息的线
     * 程池，定义了接收“ topicl ”渠道的消息，这样系统就可以监听 Redis 关于“ topicl ＂渠道的消息了 。
     */
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     *     Redis消息监听器
     */
    @Resource
    private MessageListener redisMsgListener;

    //任务池
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 创建任务池，运行线程等待处理redis的消息
     */
    @Bean
    public ThreadPoolTaskScheduler initTaskScheduler() {
        if (null != taskScheduler) {
            return taskScheduler;
        }
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        return taskScheduler;
    }

    /**
     * 定义redis的监听器
     * @return 监听容器
     */
    @Bean
    public RedisMessageListenerContainer initRedisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        //Redis连接工厂
        container.setConnectionFactory(redisConnectionFactory);
        //设置运行任务的线程池
        container.setTaskExecutor(initTaskScheduler());
        //定义监听渠道，名称为topic1
        Topic topic = new ChannelTopic("topic1");
        //使用监听器监听Redis的消息
        container.addMessageListener(redisMsgListener, topic);
        return container;
    }
}
