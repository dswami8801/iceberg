package com.esphere.iceberg.support;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.esphere.iceberg.payloads.EventType;

public class OutputChannel {

	private static Map<ServerNode, BlockingQueue<RecordWrapper>> map = new ConcurrentHashMap<>();

	public static void put(RecordWrapper recordWrapper) {
		ServerNode node = recordWrapper.getKey();
		BlockingQueue<RecordWrapper> queue = map.get(node);
		if (queue == null) {
			queue = new ArrayBlockingQueue<>(1000);
			map.put(node, queue);
		}
		try {
			queue.put(recordWrapper);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static BlockingQueue<RecordWrapper> getQueue(ServerNode node) {
		return map.get(node);
	}

	public static Set<ServerNode> getKeys() {
		return map.keySet();
	}

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 1000000000; i++) {
			put(new RecordWrapper(new ServerNode("localhost",8080), new Record(""+i%1000, ""+i%100, ""+i), null, EventType.PUT_DATA));
			put(new RecordWrapper(new ServerNode("localhost",8080), new Record(""+i%1000, ""+i%100, ""+i), null, EventType.WRITE_REPLICA));
			put(new RecordWrapper(new ServerNode("localhost",9090), new Record(""+i%1000, ""+i%100, ""+i), null, EventType.PUT_DATA));
			put(new RecordWrapper(new ServerNode("localhost",9090), new Record(""+i%1000, ""+i%100, ""+i), null, EventType.WRITE_REPLICA));
			TimeUnit.MILLISECONDS.sleep(10);
		}
	}
}
