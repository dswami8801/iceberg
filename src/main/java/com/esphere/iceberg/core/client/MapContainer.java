package com.esphere.iceberg.core.client;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.esphere.iceberg.payloads.DataPayload;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.support.Node;
import com.esphere.iceberg.support.Record;
import com.esphere.iceberg.util.HashFunction;

import io.netty.channel.ChannelFuture;

public class MapContainer<K, V> {

	private String bucketKey;

	private int partitions = 6;

	public MapContainer(String bucketKey) {
		Thread thread = new Thread(new OutPutChalnnelConsumer());
		thread.start();
		this.bucketKey = bucketKey;
	}

	public V putItem(K key, V value) {

		long partitionHash = HashFunction.calculate((String) key) % partitions;

		String partitionKey = getPartitionKey(partitionHash);

		Node node = getLeaderNode(HashFunction.calculate(partitionKey));
		Record record = new Record(partitionKey, (String) key, value);
		pushItem(node, record);

		return value;
	}

	public Future<Record> getItem(Object key) {

		long partitionHash = HashFunction.calculate((String) key) % partitions;

		String partitionKey = getPartitionKey(partitionHash);

		Node node = getLeaderNode(HashFunction.calculate(partitionKey));
		Record record = new Record(partitionKey, (String) key, null);
		Future<Record> value = getItem(node, new Event(EventType.GET_DATA, new DataPayload(record)));

		return value;
	}

	private Future<Record> getItem(Node node, Event event) {
		NIOClient client = new NIOClient(node.getServerNode());
		Future<Record> future = client.send(event);
		return future;

	}

	private void pushItem(Node node, Record event) {
		RequestContainer.put(node, event);

	}

	public void enqueue(Node node, Event event) {

	}

	private String getPartitionKey(long partitionHash) {
		return bucketKey + "-" + partitionHash;
	}

	public Node getLeaderNode(long hash) {
		Set<Node> nodes = Members.MEMBERS.getMemberSet();
		for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			long[] range = node.getHashRange();
			if (hash >= range[0] && hash < range[1]) {
				return node;
			}
		}

		return null;
	}

}
