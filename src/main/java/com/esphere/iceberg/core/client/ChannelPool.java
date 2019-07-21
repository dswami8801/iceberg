package com.esphere.iceberg.core.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public enum ChannelPool {

	POOL;

	private Map<String, Channel> channels = new ConcurrentHashMap<>(10);

	private EventLoopGroup group;

	public Channel getChannel(String host, int port) {
		Channel channel = null;
		channel = channels.get(host + ":" + port);
		if (channel != null && channel.isOpen()) {
			return channel;
		}

		group = new NioEventLoopGroup();

		try {
			Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
					.handler(new ClientInitializer());
			channel = bootstrap.connect(host, port).sync().channel();
			channels.put(host + ":" + port, channel);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return channel;
	}

}
