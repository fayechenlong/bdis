package com.beeplay.bdis.server.bcache;

import com.beeplay.bdis.server.bcache.base.BaseCacheAbstract;
import com.beeplay.bdis.server.bcache.string.StringCache;
import com.beeplay.bdis.server.command.BdisCommand;

public class Bcache extends BaseCacheAbstract{


    public StringCache stringCache;

    public Bcache(){
        initCache();
    }

    public void initCache(){
        super.init();
        this.stringCache=new StringCache(super.createStringCache(CacheName.STRING_CACHE_NAME));
    }

    public String get(String key){
        Object obj=stringCache.getStringCache().get(key);
        if(obj!=null){
            return obj.toString();
        }
        return BdisCommand.NULL.cmd();
    }
    public Long set(String key,Object value){
        stringCache.getStringCache().put(key,value);
        return 1L;
    }
}
