package redis.clients.jedis.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import redis.clients.exceptions.JedisConnectionException;
import redis.clients.jedis.support.network.Connection;

public class ConnectionTest extends Assert {
    private Connection client;

    @Before
    public void setUp() throws Exception {
	client = new Connection();
    }

    @After
    public void tearDown() throws Exception {
	client.disconnect();
    }

    @Test(expected = JedisConnectionException.class)
    public void checkUnkownHost() {
	client.setHost("someunknownhost");
	client.connect();
    }

    @Test(expected = JedisConnectionException.class)
    public void checkWrongPort() {
	client.setHost("localhost");
	client.setPort(55665);
	client.connect();
    }

    @Test
    public void connectIfNotConnectedWhenSettingTimeoutInfinite() {
	client.setHost("localhost");
	client.setPort(6379);
	client.setTimeoutInfinite();
    }

    @Test
    public void checkCloseable() {
	client.setHost("localhost");
	client.setPort(6379);
	client.connect();
	client.close();
    }
}