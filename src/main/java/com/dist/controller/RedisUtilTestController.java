package com.dist.controller;

import com.dist.service.RedisMethodsCacheService;
import com.dist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author zhengja@dist.com.cn
 * @data 2019/6/17 17:28
 */
@RestController
@RequestMapping(value = "rest/redis/util")
@Api(tags = {"RedisUtilTestController"}, description = "redisUtil 工具测试")
public class RedisUtilTestController {

    @Resource
    private RedisUtil redisUtil;

    @RequestMapping(value = "v1/redisSave",method = {RequestMethod.GET})
    @ApiOperation(value = "保存到redis,并返回结果",notes = "保存到redis")
    public Object redisSave(@ApiParam(value = "传入key值") @RequestParam String key,
                        @ApiParam(value = "传入value值") @RequestParam String value){
        redisUtil.set(key,value);
        return redisUtil.get(key);
    }

    @RequestMapping(value = "v1/redisGetAllKeyValue",method = {RequestMethod.GET})
    @ApiOperation(value = "获取redis种所有key和value值",notes = "获取redis种所有key和value值")
    public Object redisGetAllKeyValue(){
        Set<String> keys = redisUtil.keys("*");
        Iterator<String> iterator = keys.iterator();
        Map<String,Object> map = new HashMap<>();
        while (iterator.hasNext()){
            String key = iterator.next();
            Object o = redisUtil.get(key);
            map.put(key,o);
        }
        return map;
    }


    @RequestMapping(value = "v1/deleteRedisAll",method = {RequestMethod.DELETE})
    @ApiOperation(value = "清除redis所有缓存",notes = "清除redis所有缓存")
    public Object deleteRedisAll(){
        Set<String> keys = redisUtil.keys("*");
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            redisUtil.del(iterator.next());
        }
        return "删除redis所有数据成功";
    }

    @RequestMapping(value = "v1/getRedisValue",method = {RequestMethod.GET})
    @ApiOperation(value = "根据key获取value值",notes = "根据key获取value值")
    public Object getRedisValue(@ApiParam(value = "传入key值") @RequestParam String key){
        return redisUtil.get(key);
    }

}
