package com.esphere.iceberg.core.client;

import java.util.concurrent.Future;

import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.support.Record;
import com.esphere.iceberg.support.ServerNode;

import io.netty.channel.Channel;

public class NIOClient {

	private Channel channel;

	private String host;

	private int port;

	public NIOClient(ServerNode serverNode) {
		this.host = serverNode.getHost();
		this.port = serverNode.getPort();
		channel = ChannelPool.POOL.getChannel(host, port);

	}

	public NIOClient(String host, int port) {
		this.host = host;
		this.port = port;
		channel = ChannelPool.POOL.getChannel(host, port);

	}

	public Future<Record> send(Event event) {
		try {
			RecordFuture<Record> future = new RecordFuture<Record>();
			future.setRequestId(event.getEventId());
			ClientEventHandler clientEventHandler = ((ClientEventHandler) channel.pipeline().get("handler2"));
			clientEventHandler.setFuture(future);
			channel.write(event);
			channel.flush();
			return future;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
