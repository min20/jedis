package redis.clients.jedis.tests.utils;

import java.util.concurrent.atomic.AtomicReference;

import redis.clients.host.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.pubsub.JedisPubSub;
import redis.clients.jedis.tests.utils.FailoverAbortedException;

public class JedisSentinelTestUtil {
    public static HostAndPort waitForNewPromotedMaster(Jedis sentinelJedis) 
	    throws InterruptedException {
	
	final AtomicReference<String> newmaster = new AtomicReference<String>(
		"");

	sentinelJedis.psubscribe(new JedisPubSub() {

	    @Override
	    public void onMessage(String channel, String message) {
	    }

	    @Override
	    public void onPMessage(String pattern, String channel,
		    String message) {
		if (channel.equals("+switch-master")) {
		    newmaster.set(message);
		    punsubscribe();
		} else if (channel.startsWith("-failover-abort")) {
		    punsubscribe();
		    throw new FailoverAbortedException("Unfortunately sentinel cannot failover... reason(channel) : " + 
			    channel + " / message : " + message);
		}
	    }

	    @Override
	    public void onSubscribe(String channel, int subscribedChannels) {
	    }

	    @Override
	    public void onUnsubscribe(String channel, int subscribedChannels) {
	    }

	    @Override
	    public void onPUnsubscribe(String pattern, int subscribedChannels) {
	    }

	    @Override
	    public void onPSubscribe(String pattern, int subscribedChannels) {
	    }
	}, "*");

	String[] chunks = newmaster.get().split(" ");
	HostAndPort newMaster = new HostAndPort(chunks[3],
		Integer.parseInt(chunks[4]));

	return newMaster;
    }

}
