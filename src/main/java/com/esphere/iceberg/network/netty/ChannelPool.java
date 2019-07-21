package com.esphere.iceberg.network.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.esphere.iceberg.core.Cluster;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

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
			channel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println(
							"ChannelPool.getChannel(...).new GenericFutureListener() {...}.operationComplete()");
					POOL.channels.remove(host + ":" + port);
					Cluster.getMasterList().getNodes().get(host + ":" + port).getServerNode().setAlive(false);
					
				}
			});
			channels.put(host + ":" + port, channel);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return channel;
	}

}
