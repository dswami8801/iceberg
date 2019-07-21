package com.esphere.iceberg.network.netty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.esphere.iceberg.payloads.DataPayload;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.support.Record;
import com.esphere.iceberg.support.ServerNode;
import com.esphere.iceberg.util.HashFunction;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

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

	public ChannelFuture send(Event event) {

		try {
			if (!channel.isActive()) {
				System.out.println("Host not reachable :" + channel.localAddress());
				return null;
			}
			return channel.write(event).sync();
		} catch (Exception e) {
			System.out.println("Host not reachable" + channel.localAddress());
		}
		return null;

	}

	public void closeConnection() {
		// channel.close();

	}

	public void openConnection() throws IOException {

	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		try {
			put();
			// System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Map<ServerNode, List<Record>> map = new HashMap<ServerNode, List<Record>>();

//			localhost:9090      |true      |true      |0         |35        |[0, 2305843009213693952]
//			localhost:8080      |true      |true      |0         |26        |[4611686018427387904, 9223372036854775807]
//			localhost:7070      |true      |false     |0         |25        |[2305843009213693952, 4611686018427387904]
	public static void put(Record record) {
		long hash = HashFunction.calculate(record.bucketKey);

		if (hash >= 0 && hash <= 2305843009213693952l) {
			map.get(new ServerNode("localhost", 9090)).add(record);
		} else if (hash > 2305843009213693952l && hash <= 4611686018427387904l) {
			map.get(new ServerNode("localhost", 7070)).add(record);
		} else if (hash > 4611686018427387904l && hash <= 9223372036854775807l) {
			map.get(new ServerNode("localhost", 8080)).add(record);
		}
	}

	public static void flush() {
		map.entrySet().forEach(e -> {
			NIOClient nioClient = new NIOClient(e.getKey());
			nioClient.send(new Event(EventType.PUT_DATA, new DataPayload(e.getValue())));

		});
	}

	public static void put() throws Exception {
		map.put(new ServerNode("localhost", 9090), new ArrayList<Record>());
		map.put(new ServerNode("localhost", 8080), new ArrayList<Record>());
		map.put(new ServerNode("localhost", 7070), new ArrayList<Record>());

		long start = System.currentTimeMillis();
		NIOClient chatClient = new NIOClient("localhost", 9090);

		for (int k = 0; k <= 200; k++) {

			for (int j = 0; j < 10; j++) {
				for (int i = 0; i < 1000; i++) {

					put(new Record("city-" + j, UUID.randomUUID().toString(),
							"Mumbai : " + UUID.randomUUID().toString()));
				}

			}
			flush();
			TimeUnit.MILLISECONDS.sleep(100);
			map.entrySet().forEach(e -> {
				e.getValue().clear();

			});
			System.out.println(TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - start)));
		}

		chatClient.closeConnection();

	}

}
