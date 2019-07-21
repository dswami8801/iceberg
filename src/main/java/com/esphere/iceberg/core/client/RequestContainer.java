package com.esphere.iceberg.core.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.support.Node;
import com.esphere.iceberg.support.Record;

public class RequestContainer {

	private static Map<Node, BlockingQueue<Record>> map = new HashMap<>();

	public static void put(Node node, Record event) {
		BlockingQueue<Record> blockingQueue = map.get(node);
		if (blockingQueue == null) {
			blockingQueue = new ArrayBlockingQueue<>(100);
			map.put(node, blockingQueue);
		}
		try {
			blockingQueue.put(event);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static BlockingQueue<Record> getQueue(Node node) {
		return map.get(node);
	}

	public static Set<Node> getKeys() {
		Set<Node> nodes = new HashSet<>(map.keySet());
		return nodes;
	}

}
