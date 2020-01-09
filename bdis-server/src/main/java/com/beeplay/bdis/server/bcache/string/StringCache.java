package com.beeplay.bdis.server.bcache.string;
import org.ehcache.Cache;

public class StringCache {

    private Cache<String,String> stringCache;

    public StringCache(){

    }

    public StringCache(Cache<String,String> stringCache){
        this.stringCache=stringCache;
    }

    public Cache<String,String> getStringCache(){
        return this.stringCache;
    }

}
