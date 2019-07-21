package com.esphere.iceberg.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.esphere.iceberg.network.ServerConfig;
import com.esphere.iceberg.network.netty.NIOClient;
import com.esphere.iceberg.network.netty.NIOServer;
import com.esphere.iceberg.payloads.DataPayload;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.ReplicaPayload;
import com.esphere.iceberg.persistence.DataRepository;
import com.esphere.iceberg.support.MembershipList;
import com.esphere.iceberg.support.Node;
import com.esphere.iceberg.support.OutPutChalnnelConsumer;
import com.esphere.iceberg.support.OutputChannel;
import com.esphere.iceberg.support.Record;
import com.esphere.iceberg.support.RecordWrapper;
import com.esphere.iceberg.support.Replica;
import com.esphere.iceberg.support.ReplicaSet;
import com.esphere.iceberg.support.ServerNode;
import com.esphere.iceberg.util.HashFunction;
import com.esphere.iceberg.workers.AllocationWorker;
import com.esphere.iceberg.workers.GossipWorker;
import com.esphere.iceberg.workers.HeartBeatWorker;
import com.esphere.iceberg.workers.RebalanceWorker;

public class Cluster {

	private int replicationFactor = 1;
	public static List<ServerNode> serverNodes = new ArrayList<>();
	public static MembershipList membershipList = new MembershipList();
	public static String id;
	public static ServerNode me;
	public static int updated = 0;
	public static ExecutorService executorService = Executors.newFixedThreadPool(10);

	public Cluster me(ServerNode serverNode) {
		serverNodes.add(serverNode);
		me = serverNode;
		id = me.getId();
		return this;
	}

	public static void prettyPrint() {
		prettyPrint(membershipList);
	}

	public static void prettyPrint(MembershipList masterList) {
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
		System.out.println(String.format("%-20s|%-10s|%-10s|%-10s|%-10s|%-40s", "Id", "Alive", "Allocated",
				"FailedThreshold", "Heartbeat", "HashRange"));
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
		masterList.getNodes().values().forEach(ns -> {
			ServerNode sn = ns.getServerNode();
			System.out.println(
					String.format("%-20s|%-10s|%-10s|%-10s|%-10s|%-40s", sn.getId(), ns.isAlive(), ns.isAllocated(),
							ns.getFailedThreshold(), ns.getHeartbeatCount(), Arrays.toString(sn.getHashRange())));
		});

		Node.prettyPrint(masterList);
	}

	public static ServerNode me() {
		ServerNode node = null;
		for (Node nodeStatistics : membershipList.getNodes().values()) {
			if (Cluster.me.equals(nodeStatistics.getServerNode())) {
				return nodeStatistics.getServerNode();
			}
		}
		return node;
	}

	public Cluster withServerNode(ServerNode serverNode) {
		serverNode.setMaster(true);
		serverNodes.add(serverNode);
		return this;
	}

	public static MembershipList getMasterList() {
		return membershipList;
	}

	public static Collection<Node> getNodeStatistics() {
		return membershipList.getNodes().values();
	}

	public void provision() {
		if (replicationFactor <= 0 || replicationFactor > serverNodes.size()) {
			throw new IllegalArgumentException(String
					.format("Invalid value for replication factor, must be in range %d-%d", 1, serverNodes.size()));
		}

		membershipList = new MembershipList();
		serverNodes.forEach(sn -> {
			membershipList.add(new Node(sn));
		});
		if (!me.isMaster()) {
			new AllocationWorker().advertise();
		} else {
			ClusterManager.allocate(me);
			executorService.submit(new RebalanceWorker());
		}

		executorService.submit(new GossipWorker());
		executorService.submit(new HeartBeatWorker());
		executorService.submit(new OutPutChalnnelConsumer());

		ServerConfig config = new ServerConfig();
		config.setPort(me.getPort());
		NIOServer nioServer = new NIOServer(config);
		nioServer.start();
		System.out.println("Cluster.provision()");

	}

	public Cluster withReplicationFactor(int replicationFactor) {
		this.replicationFactor = replicationFactor;
		return this;
	}

	public static void put(List<Record> records) {
		System.out.println("Received data put request with  Size :" + records.size());
		records.forEach(record -> {
			long hash = HashFunction.calculate(record.getBucketKey());
			// System.out.println(String.format("Putting data with key:hash : %s:%s", key,
			// hash));
			ReplicaSet replicaSet = ClusterManager.findServer(hash, 3);
			// System.out.println(String.format("Found server with hash range : %s",
			// replicaSet));
			putData(replicaSet, record.getBucketKey(), record);

		});

//		Set<ServerNode> strings = OutputChannel.getKeys();
//		strings.forEach(sn -> {
//			System.out.println("Processing queue " + sn.getId());
//			BlockingQueue<RecordWrapper> queue = OutputChannel.getQueue(sn);
//			if (queue.size() > 0) {
//
//				ChatClient client = new ChatClient(sn);
//				try {
//					client.openConnection();
//					List<Record> list = queue.stream().map(rw -> rw.getRecord()).collect(Collectors.toList());
//					client.send(new Event(EventType.PUT_DATA, new DataPayload(key, list)));
//					client.closeConnection();
//				} catch (IOException e) {
//				}
//				queue.clear();
//			}
//		});

	}

	public static void replicate(ReplicaPayload replicaPayload) {
		System.out.println(String.format("Replicating data with key : %s", replicaPayload.getRecords().size()));
		writeReplica(Cluster.me, replicaPayload);

	}

	public static void writeReplica(ServerNode serverNode, ReplicaPayload replicaPayload) {
		System.out.println("Writing replica ");
		List<Replica> replicas = serverNode.getReplicas();
		List<Record> records = replicaPayload.getRecords();
		for (Iterator<Record> iterator = records.iterator(); iterator.hasNext();) {
			Record record = iterator.next();
			
			if (!replicas.contains(new Replica(record.getBucketKey()))) {
				Replica replica = new Replica();
				replica.setKey(record.getBucketKey());
				replica.setLeader(record.getReplica().getLeader());
				replica.setType("Follower");
				replica.setSequenceNo(0);
				replica.setFollowers(record.getReplica().getFollowers());
				DataRepository.DATA.add(record.getBucketKey(), record);
				serverNode.getReplicas().add(replica);
			} else {
				Replica replica = replicas.get(replicas.indexOf(new Replica(record.getBucketKey())));
				replica.setSequenceNo(replica.getSequenceNo() + 1);
				DataRepository.DATA.add(record.getBucketKey(), record);
			}
		}

		System.out.println("Writing replica Done");
	}

	public static void putData(ReplicaSet replicaSet, String bucketKey, Record record) {
		if (replicaSet.getLeader().equals(Cluster.me())) {
			List<Replica> replicas = Cluster.me().getReplicas();
			Optional<Replica> replicaOption = replicas.stream().filter(r -> r.getKey().equalsIgnoreCase(bucketKey))
					.findFirst();
			Replica replica = null;
			if (!replicaOption.isPresent()) {
				replica = new Replica();
				replica.setType("Leader");
				replica.setKey(bucketKey);
				replica.setSequenceNo(0);
				replica.setLeader(replicaSet.getLeader());
				replica.setFollowers(replicaSet.getFollowers());
				DataRepository.DATA.add(bucketKey, record);
				Cluster.me().getReplicas().add(replica);
			} else {
				replica = replicaOption.get();
				replica.setSequenceNo(replica.getSequenceNo() + 1);
				DataRepository.DATA.add(bucketKey, record);
			}
			// write replica to each follower
			for (ServerNode node : replicaSet.getFollowers()) {
				Replica freplica = new Replica();
				freplica.setType("Follower");
				freplica.setKey(bucketKey);
				freplica.setSequenceNo(0);
				freplica.setLeader(replicaSet.getLeader());
				freplica.setFollowers(replicaSet.getFollowers());
				record.setReplica(freplica);
				OutputChannel.put(new RecordWrapper(node, record, replicaSet, EventType.WRITE_REPLICA));
//				ChatClient client = new ChatClient(node);
//				try {
//					client.openConnection();
//					client.send(new Event(EventType.WRITE_REPLICA, new ReplicaPayload(replica, key, record)));
//					client.closeConnection();
//				} catch (IOException e) {
//				}
			}

		} else {

			OutputChannel.put(new RecordWrapper(replicaSet.getLeader(), record, replicaSet, EventType.PUT_DATA));

		}

	}

	public static Record get(String key, String objectKey) {
		long hash = HashFunction.calculate(key);
		// System.out.println(String.format("Getting data with key:hash : %s:%s", key,
		// hash));
		ServerNode serverNodes = ClusterManager.findServer(hash);
		// System.out.println(String.format("Found server with hash range : %s",
		// serverNodes));
		return DataRepository.DATA.get(key, objectKey);
	}

}
