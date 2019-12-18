package com.dist.controller;

import com.dist.service.RedisMethodsCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ZhengJa
 * @description Redis 接口和方法缓存数据
 * @data 2019/11/13
 */
@RestController
@RequestMapping(value = "rest/cache")
@Api(tags = {"RedisInterfaceCacheController"}, description = "redis 接口缓存")
public class RedisInterfaceCacheController {

    @Resource
    private RedisMethodsCacheService redisMethodsCacheService;

    @RequestMapping(value = "v1/save/redis", method = {RequestMethod.GET})
    @ApiOperation(value = "新增接口", notes = "新增接口：每次新增数据，要清空缓存组，保证缓存与数据库同步")
    public Object savaCacheRedis(@ApiParam(value = "传入key值") @RequestParam String key) {
        return redisMethodsCacheService.saveData(key);
    }

    @RequestMapping(value = "v1/get/redis", method = {RequestMethod.GET})
    @ApiOperation(value = "查询接口", notes = "查询接口：首次查询缓存中没有，再到数据库查询，将查询返回的结果保存到缓存中，下次请求在缓存中取数据")
    public Object getCacheRedis(@ApiParam(value = "传入key值") @RequestParam String key) {
        return redisMethodsCacheService.getData(key);
    }

    @RequestMapping(value = "v1/put/redis", method = {RequestMethod.GET})
    @ApiOperation(value = "更新接口", notes = "更新接口：同时更新缓存库")
    public Object putCacheRedis(@ApiParam(value = "传入key值") @RequestParam String key) {
        return redisMethodsCacheService.putData(key);
    }

    @RequestMapping(value = "v1/delete/redis", method = {RequestMethod.GET})
    @ApiOperation(value = "删除数据", notes = "删除数据：同时删除缓存库中对应组的某个缓存 ，目的保证数据一致性")
    public Object deleteCacheRedis(@ApiParam(value = "传入key值") @RequestParam String key) {
        return redisMethodsCacheService.deleteData(key);
    }

    @RequestMapping(value = "v1/delete/all/redis", method = {RequestMethod.GET})
    @ApiOperation(value = "删除所有数据", notes = "删除所有数据：同时删除缓存库中对应组下面所有的缓存 ，目的保证数据一致性")
    public Object deleteAllCacheRedis(@ApiParam(value = "传入key值") @RequestParam String key) {
        return redisMethodsCacheService.deleteAllData(key);
    }

}
