
# springboot-test-redis

[toc]

项目类型：springboot+redis单数据源

主要功能：
- 1、提供redis工具类
- 2、redis存储测试
- 3、redis消息发布和监听
- 4、redis接口缓存返回数据
- 5、集成swagger2-api测试接口

工具类和redis存储测试已经在项目实战中讲了，
springboot+redis项目实战完整篇 参考地址：
https://www.jianshu.com/p/5596c3a4978d


### 3、redis消息发布和监听
发送消息
```java
package com.dist.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhengja@dist.com.cn
 * @data 2019/6/11 14:05
 */
@Api(tags = {"RedisSendMessageController"}, description = "redis发布测试")
@RestController
@RequestMapping(value = "rest/redis")
public class RedisSendMessageController {
    //传string会出现乱码
    @Resource
    private RedisTemplate redisTemplate;
    //解决string乱码问题
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "redis-发布", notes = "redis-发布")
    @RequestMapping(value = "send/message", method = RequestMethod.GET)
    public void testPush(@ApiParam(value = "发布的消息内容", required = true) @RequestParam("body") String body){
        /**
         * 使用redisTemplate的convertAndSend()函数，
         * String channel, Object message
         * channel代表管道，
         * message代表发送的信息
         */
        System.out.println("body==="+body);
        stringRedisTemplate.convertAndSend("topic1", body);
    }
}

```
监听器：接收消息
```java
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

```

### 4、redis接口缓存返回数据
直接上代码，具体效果自己测试，将此项目下载可以测试
```java
package com.dist.service.impl;

import com.dist.constants.RedisConstants;
import com.dist.service.RedisMethodsCacheService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author ZhengJa
 * @description
 * @data 2019/11/13
 */
@Service
@CacheConfig(cacheNames = RedisConstants.EntityCacheKey.OPINION)
public class RedisMethodsCacheServiceImpl implements RedisMethodsCacheService {

    //value:组, key:组员
    //@Cacheable(value = "getData1value", key = "'getData1key:'+#id", unless = "#result==null")
    //@Cacheable(value = "getData1value",key = "'getData1key'")
    @Cacheable(key = "'getData1key'")
    @Override
    public String getData(){
        return "redis缓存-无参：方法返回的数据";
    }

    /**
     * 多个参数查询 key的写法
     * @param page  参数1
     * @param pageSize 参数2
     * @return Object
     */
    @Cacheable(key = "#page+'-'+#pageSize")
    public Object findAllUsers(int page,int pageSize) {
        int pageStart = (page-1)*pageSize;
        return null;
    }

    /**  Cacheable : value-组, key-组员
     * 新增接口：每次新增数据，要清空缓存组，保证缓存与数据库同步
     * @param parameter
     * @return java.lang.String
     */
    @Cacheable(key = "'rediskey:'+#p0")
    @Override
    public String saveData(String parameter){
        return "redis-新增: "+parameter;
    }

    /** Cacheable : value-组, key-组员
     * 查询接口：首次查询缓存中没有，再到数据库查询，将查询返回的结果保存到缓存中，下次请求在缓存中取数据
     * @param parameter
     * @return java.lang.String
     */
    //@Cacheable(value = "getData2value",key = "'getData2key:'+#parameter")
    @Cacheable(key = "'rediskey:'+#p0")
    //@Cacheable(key = "'rediskey:'+#p0",keyGenerator = "wiselyKeyGenerator")
    @Override
    public String getData(String parameter){
        return "redis-查询: "+parameter;
    }

    /**
     * 更新接口：同时更新缓存库
     * @param parameter
     * @return java.lang.String
     */
    //@CachePut(key = "'rediskey:'+#p0")
    @CachePut(key = "'rediskey:'+#p0")
    @Override
    public String putData(String parameter){
        return "redis-更新: "+parameter;
    }

    /**
     * 删除数据：同时删除缓存库中对应的 组 ，目的保证数据一致性
     * @param parameter
     * @return java.lang.String
     */
    //@CacheEvict(allEntries=true)// 清空组下的所有缓存
    @CacheEvict(key = "'rediskey:'+#p0")// 清空组下的某个缓存
    @Override
    public String deleteData(String parameter){
        return "redis-删除某个缓存: "+parameter;
    }

    /**
     * 删除数据：同时删除缓存库中对应的 组 ，目的保证数据一致性
     * @param parameter
     * @return java.lang.String
     */
    // 清空组下的所有缓存,忽略key
    //@CacheEvict(value=RedisConstants.EntityCacheKey.OPINION, allEntries=true)
    @CacheEvict(allEntries=true)
    @Override
    public String deleteAllData(String parameter){
        return "redis-清除组："+RedisConstants.EntityCacheKey.OPINION;
    }

}

```



