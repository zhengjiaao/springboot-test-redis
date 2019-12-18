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
