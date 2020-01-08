package com.beeplay.bdis.server.bcache.string;
import org.ehcache.Cache;

public class StringCache {

    private Cache<String,Object> stringCache;
    public StringCache(Cache<String,Object> stringCache){
        this.stringCache=stringCache;
    }

    public Cache<String,Object> getStringCache(){
        return this.stringCache;
    }

}
