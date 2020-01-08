package com.beeplay.bdis.server.bcache.map;

import com.beeplay.bdis.server.bcache.base.BaseCacheAbstract;
import org.ehcache.Cache;

import java.util.Map;

public class MapCache extends BaseCacheAbstract {

    private Cache<String,Map> stringCache;
    public MapCache(Cache<String,Map> stringCache){
        this.stringCache=stringCache;
    }

}
