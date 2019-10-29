package com.beeplay.bdis.server.utils;

import org.apache.log4j.Logger;
import redis.clients.jedis.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public  class RedisManagerUtil {
	private static final Logger logger = Logger.getLogger(RedisManagerUtil.class);
	private static  RedisManagerUtil instance;
	private  int MAX_ACTIVE = 8;
	private  int MAX_IDLE = 8;
	private  int MAX_WAIT = 10000;
	private  int TIMEOUT = 10000;
	private  boolean TEST_ON_BORROW = true;
	private JedisPool jedisPool = null;

	public static RedisManagerUtil getInstance(String host,int port,String pass) {
		if (instance == null) {
			synchronized (RedisManagerUtil.class) {
				if (instance == null) {
					instance = new RedisManagerUtil(host, port, pass);
				}
			}
		}
		return instance;
	}
		private RedisManagerUtil (String host,int port,String pass){
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxIdle(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			if(pass!=null&&!"".equals(pass)) {
				jedisPool = new JedisPool(config, host, port, TIMEOUT, pass);
			}else{
				jedisPool = new JedisPool(config, host, port, TIMEOUT);
			}

		}
	
	/**
	 * 释放jedis资源
	 * @param jedis
	 */
	public  void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.close();
		}
	}
	public Long incr(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.incr(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}

		return null;
	}

	public Long incr(String key, Long integer) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.incrBy(key, integer);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}

		return null;
	}

	public String get(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.get(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}

		return null;
	}

	public String set(String key, String value) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.set(key, value);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}

		return null;
	}

	public String setObject(String key, Object value) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String objectValue = GfJsonUtil.toJSONString(value);
			String v = sj.set(key, objectValue);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}

		return null;
	}

	public void set(String key, List<String> strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (String s : strings) {
				pip.set(key, s);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public String getSet(String key, String value) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.getSet(key, value);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long sadd(String key, String... members) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.sadd(key, members);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long zadd(String key, Map<String, Double> map) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.zadd(key, map);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public void zadd(String key, List<Map<String, Double>> maps) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (Map<String, Double> map : maps) {
				pip.zadd(key, map);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public void sadd(String key, List<String> strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (String s : strings) {
				pip.sadd(key, s);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public String hget(String key, String field) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.hget(key, field);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public String hmset(String key, Map<String, String> hash) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.hmset(key, hash);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public void hmset(String key, List<Map<String, String>> hash) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (Map<String, String> h : hash) {
				pip.hmset(key, h);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public List<String> hmget(String key, String... fields) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			List<String> v = sj.hmget(key, fields);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long del(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.del(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long decr(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.decr(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long decr(String key, Long integer) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.decrBy(key, integer);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long append(String key, String value) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.append(key, value);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long bitcount(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.bitcount(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long bitpos(String key, boolean bool) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.bitpos(key, bool);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long expire(String key, int seconds) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.expire(key, seconds);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long expireAt(String key, Long unixTime) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.expireAt(key, unixTime);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long hdel(String key, String... fields) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.hdel(key, fields);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long hincrBy(String key, String field, Long value) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.hincrBy(key, field, value);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long hlen(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.hlen(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long hset(String key, String field, String value) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.hset(key, field, value);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public void hset(String key, String field, List<String> strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (String s : strings) {
				pip.hset(key, field, s);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public Long hsetnx(String key, String field, String value) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.hsetnx(key, field, value);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public void hsetnx(String key, String field, List<String> strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (String s : strings) {
				pip.hsetnx(key, field, s);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public Long llen(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.llen(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long lpush(String key, String... strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.lpush(key, strings);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public void lpush(String key, List<String> strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (String s : strings) {
				pip.lpush(key, s);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public Long lpushx(String key, String string) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.lpushx(key, string);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public void lpushx(String key, List<String> strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (String s : strings) {
				pip.lpushx(key, s);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public String lindex(String key, int index) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.lindex(key, index);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public String lpop(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.lpop(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public List<String> lpop(String key, int start, int end) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			List<String> v = sj.lrange(key, start, end);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long lrem(String key, int count, String value) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.lrem(key, count, value);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long move(String key, int dbIndex) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.move(key, dbIndex);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long persist(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.persist(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long persist(String key, Long milliseconds) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.pexpire(key, milliseconds);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long pexpireAt(String key, Long milliseconds) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.pexpireAt(key, milliseconds);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long pfadd(String key, String... elements) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.pfadd(key, elements);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long pttl(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.pttl(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public Long pfcount(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.pfcount(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long rpush(String key, String... strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.rpush(key, strings);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}
	public Long rpushNoLog(String key, String strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.rpush(key, strings);
			return v;
		} catch (Exception e) {
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}
	public void rpush(String key, List<String> strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (String s : strings) {
				pip.rpush(key, s);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public void rpushx(String key, List<String> strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Pipeline pip = sj.pipelined();
			for (String s : strings) {
				pip.rpushx(key, s);
			}
			pip.sync();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}

	public Long rpushx(String key, String... strings) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.rpushx(key, strings);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public String rpop(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.rpop(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Double zscore(String key, String member) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Double v = sj.zscore(key, member);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Set<String> smembers(String key, String member) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Set<String> v = sj.smembers(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Boolean sismember(String key, String member) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Boolean v = sj.sismember(key, member);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Boolean exists(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Boolean v = sj.exists(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public String echo(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			String v = sj.echo(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Set<String> zrange(String key, Long start, Long end) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Set<String> v = sj.zrange(key, start, end);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long zrem(String key, String... members) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.zrem(key, members);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Set<String> zrange(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Set<String> v = sj.zrange(key, 0, -1);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Set<String> zrevrange(String key, Long start, Long end) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Set<String> v = sj.zrevrange(key, start, end);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Set<String> zrevrange(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Set<String> v = sj.zrevrange(key, 0, -1);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Map<String, String> hgetAll(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Map<String, String> v = sj.hgetAll(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public List<String> brpop(String arg) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			List<String> v = sj.brpop(arg);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public List<String> sort(String key) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			List<String> v = sj.sort(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public List<String> srandmember(String key, int count) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			List<String> v = sj.srandmember(key, count);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}

	public Long pttl(String key, int count) {
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long v = sj.pttl(key);
			return v;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}
	public void subscribe(JedisPubSub pubSub,String... channels){
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			sj.subscribe(pubSub,channels);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
	}
	public Long publish(String channel,String msg){
		Jedis sj = null;
		try {
			sj = jedisPool.getResource();
			Long re=sj.publish(channel,msg);
			return re;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (sj != null) {
				returnResource(sj);
			}
		}
		return null;
	}
}