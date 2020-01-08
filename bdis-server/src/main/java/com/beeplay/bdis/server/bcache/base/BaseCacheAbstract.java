package com.beeplay.bdis.server.bcache.base;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseCacheAbstract {

    private Long heapSize=100L;
    private Long offheapSize=100L;
    private Long diskSize=1000L;

    public CacheManager cacheManager;

    public void init(){
        this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
    }
    private  <K, V> CacheConfigurationBuilder<K, V> getConfiguration(Class<K> kType,Class<V> vType){
        return CacheConfigurationBuilder.newCacheConfigurationBuilder(kType,vType, ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(heapSize, EntryUnit.ENTRIES));
    }
    public Cache<String,Object> createStringCache(String cacheName){
        Cache<String, Object> myCache=cacheManager.createCache(cacheName,getConfiguration(String.class,Object.class));
        return myCache;
    }
    public Cache<String,Map> createMapCache(String cacheName){
        Cache<String,Map> myCache=cacheManager.createCache(cacheName,getConfiguration(String.class,Map.class));
        return myCache;
    }
    public Cache<String,Set> createSetCache(String cacheName){
        Cache<String,Set> myCache=cacheManager.createCache(cacheName,getConfiguration(String.class,Set.class));
        return myCache;
    }
    public Cache<String,List> createListCache(String cacheName){
        Cache<String,List> myCache=cacheManager.createCache(cacheName,getConfiguration(String.class,List.class));
        return myCache;
    }
    public Cache<String,String> getStringCache(String cacheName){
        Cache<String, String> myCache=cacheManager.getCache(cacheName, String.class, String.class);
        return myCache;
    }
    public Cache<String,List> getListCache(String cacheName){
        Cache<String, List> myCache=cacheManager.getCache(cacheName, String.class, List.class);
        return myCache;
    }
    public Cache<String,Map> getMapCache(String cacheName){
        Cache<String, Map> myCache=cacheManager.getCache(cacheName, String.class, Map.class);
        return myCache;
    }
    public Cache<String,Set> getSetCache(String cacheName){
        Cache<String, Set> myCache=cacheManager.getCache(cacheName, String.class, Set.class);
        return myCache;
    }
}
