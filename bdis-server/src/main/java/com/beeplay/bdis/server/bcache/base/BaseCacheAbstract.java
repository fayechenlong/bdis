package com.beeplay.bdis.server.bcache.base;

import com.beeplay.bdis.server.config.StartConfig;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseCacheAbstract {

    private Long diskSize=1000L;
    private String cacheDiskPath="/data";
    private String childName="myData";
    private boolean idDisk=false;

    public CacheManager cacheManager;
    public PersistentCacheManager persistentCacheManager;

    public void init(){
        if(idDisk) {
            this.persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                    .with(CacheManagerBuilder.persistence(new File(cacheDiskPath, childName))
                    ).build(true);
        }else {
            this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        }
    }
    private  <K, V> CacheConfigurationBuilder<K, V> getConfiguration(Class<K> kType,Class<V> vType){
        return CacheConfigurationBuilder.newCacheConfigurationBuilder(kType,vType, ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(StartConfig.heapSize, EntryUnit.ENTRIES)
                .offheap(StartConfig.offheapSize, MemoryUnit.MB));
    }
    public Cache<String,String> createStringCache(String cacheName){
        Cache<String, String> myCache;
        if(idDisk) {
            myCache = persistentCacheManager.createCache(cacheName, getConfiguration(String.class, String.class));
        }else {
            myCache = cacheManager.createCache(cacheName, getConfiguration(String.class, String.class));
        }
        return myCache;
    }
    public Cache<String,Map> createMapCache(String cacheName){
        Cache<String,Map> myCache;
        if(idDisk) {
            myCache = persistentCacheManager.createCache(cacheName, getConfiguration(String.class, Map.class));
        }else {
            myCache = cacheManager.createCache(cacheName, getConfiguration(String.class, Map.class));
        }
        return myCache;
    }
    public Cache<String,Set> createSetCache(String cacheName){
        Cache<String,Set> myCache;
        if(idDisk) {
            myCache = persistentCacheManager.createCache(cacheName, getConfiguration(String.class, Set.class));
        }else {
            myCache = cacheManager.createCache(cacheName, getConfiguration(String.class, Set.class));
        }
        return myCache;
    }
    public Cache<String,List> createListCache(String cacheName){
        Cache<String,List> myCache;
        if(idDisk) {
            myCache = persistentCacheManager.createCache(cacheName, getConfiguration(String.class, List.class));
        }else {
            myCache = cacheManager.createCache(cacheName, getConfiguration(String.class, List.class));
        }
        return myCache;
    }
    public Cache<String,String> getStringCache(String cacheName){
        Cache<String, String> myCache;
        if(idDisk) {
            myCache = persistentCacheManager.getCache(cacheName,String.class, String.class);
        }else {
            myCache = cacheManager.getCache(cacheName, String.class, String.class);
        }
        return myCache;
    }
    public Cache<String,List> getListCache(String cacheName){
        Cache<String, List> myCache;
        if(idDisk) {
            myCache = persistentCacheManager.getCache(cacheName,String.class, List.class);
        }else {
           myCache = cacheManager.getCache(cacheName, String.class, List.class);
        }
        return myCache;
    }
    public Cache<String,Map> getMapCache(String cacheName){
        Cache<String, Map> myCache;
        if(idDisk) {
            myCache = persistentCacheManager.getCache(cacheName,String.class, Map.class);
        }else {
            myCache = cacheManager.getCache(cacheName, String.class, Map.class);
        }
        return myCache;
    }
    public Cache<String,Set> getSetCache(String cacheName){
        Cache<String, Set> myCache;
        if(idDisk) {
            myCache = persistentCacheManager.getCache(cacheName,String.class, Set.class);
        }else {
            myCache = cacheManager.getCache(cacheName, String.class, Set.class);
        }
        return myCache;
    }
}
