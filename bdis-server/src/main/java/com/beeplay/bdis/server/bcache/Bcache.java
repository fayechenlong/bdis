package com.beeplay.bdis.server.bcache;

import com.beeplay.bdis.server.bcache.base.BaseCacheAbstract;
import com.beeplay.bdis.server.bcache.string.StringCache;
import com.beeplay.bdis.server.command.BdisCommand;
import org.ehcache.Cache;

public class Bcache extends BaseCacheAbstract{


    public StringCache stringCache;

    public Bcache(){
        initCache();
    }

    public void initCache(){
        super.init();
        Cache<String,String> cache=super.getStringCache(CacheName.STRING_CACHE_NAME);
        if(cache==null) {
            this.stringCache = new StringCache(super.createStringCache(CacheName.STRING_CACHE_NAME));
        }else {
            this.stringCache=new StringCache(cache);
        }
    }
    public String get(String key){
        Object obj=stringCache.getStringCache().get(key);
        if(obj!=null){
            return obj.toString();
        }
        return BdisCommand.NULL.cmd();
    }
    public Long set(String key,String value){
        stringCache.getStringCache().put(key,value);
        return 1L;
    }
    public Long del(String key){
        stringCache.getStringCache().remove(key);
        return 1L;
    }
}
