package com.esphere.iceberg.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.esphere.iceberg.support.MembershipList;
import com.esphere.iceberg.support.Node;
import com.esphere.iceberg.support.ReplicaSet;
import com.esphere.iceberg.support.ServerNode;
import com.esphere.iceberg.workers.GossipWorker;

public class ClusterManager {

	public static ReplicaSet findServer(long hash, int rFactor) {
		ReplicaSet replicaSet = new ReplicaSet();
		Iterator<Node> it = Cluster.getNodeStatistics().iterator();

		while (it.hasNext() && rFactor >= 0) {
			Node node = it.next();
			ServerNode sn = node.getServerNode();
			long[] range = sn.getHashRange();
			if (hash >= range[0] && hash <= range[1]) {
				rFactor--;
				replicaSet.setLeader(sn);
				break;
			}
		}
		it = Cluster.getNodeStatistics().iterator();
		while (it.hasNext() && rFactor >= 0) {
			ServerNode serverNode = it.next().getServerNode();
			if (!serverNode.equals(replicaSet.getLeader())) {
				rFactor--;
				replicaSet.getFollowers().add(serverNode);
			}
		}
		return replicaSet;

	}

	public static ServerNode findServer(long hash) {
		ServerNode masterNode = null;
		Iterator<Node> it = Cluster.getNodeStatistics().iterator();

		while (it.hasNext()) {
			Node node = it.next();
			ServerNode sn = node.getServerNode();
			long[] range = sn.getHashRange();
			if (hash >= range[0] && hash < range[1]) {
				masterNode = sn;
				break;
			}
		}

		return masterNode;

	}

	public static void repair(Node nodeStatistics) {

		Collection<Node> statistics = Cluster.membershipList.getNodes().values();
		Node overtakenNode = null;

		// first node has died
		if (nodeStatistics.getServerNode().getHashRange()[0] == 0) {
			System.out.println("First node in ring has died");
			long to = nodeStatistics.getServerNode().getHashRange()[1];
			for (Iterator<Node> iterator = statistics.iterator(); iterator.hasNext();) {
				Node ns = iterator.next();
				if (ns.getServerNode().getHashRange()[0] == to) {
					overtakenNode = ns;
				}
			}
			overtakenNode.getServerNode().getHashRange()[0] = 0;
			overtakenNode.setHeartbeatCount(overtakenNode.getHeartbeatCount() * 2);
			nodeStatistics.setHeartbeatCount(Integer.MIN_VALUE);
		} else {
			System.out.println("Follower node in ring has died");
			long from = nodeStatistics.getServerNode().getHashRange()[0];
			for (Iterator<Node> iterator = statistics.iterator(); iterator.hasNext();) {
				Node ns = iterator.next();
				if (ns.getServerNode().getHashRange()[1] == from) {
					overtakenNode = ns;
					System.out.println("Found node to overtake");
				}
			}
			overtakenNode.getServerNode().getHashRange()[1] = nodeStatistics.getServerNode().getHashRange()[1];
			overtakenNode.setHeartbeatCount(overtakenNode.getHeartbeatCount() * 2);
			nodeStatistics.setHeartbeatCount(Integer.MIN_VALUE);
		}
		System.out.println(String.format("Node %s overtaken the node %s", overtakenNode, nodeStatistics));
		new GossipWorker().broadcast();
	}

	public static ServerNode allocate(ServerNode serverNode) {

		if (!Cluster.me().isMaster()) {
			return serverNode;
		}
		System.out.println("Allocation range for " + serverNode);
		List<Node> nodeStatistics = new ArrayList<Node>(Cluster.getNodeStatistics());
		if (serverNode.isMaster()) {
			serverNode.setHashRange(new long[] { 0, Long.MAX_VALUE });
			serverNode.setAllocated(true);
			System.out.println("Allocated range  " + Arrays.toString(serverNode.getHashRange()));
			return serverNode;
		}
		nodeStatistics.sort((ns1, ns2) -> {
			long[] range1 = ns1.getServerNode().getHashRange();
			long[] range2 = ns2.getServerNode().getHashRange();

			return new Long(range2[1] - range2[0]).compareTo(new Long(range1[1] - range1[0]));
		});

		System.out.println("Sotrted by range");
		nodeStatistics.forEach(ns -> {
			ServerNode sn = ns.getServerNode();
			System.err.println(String.format("%10s,%10s,%10s,%10s", sn.getId(), Arrays.toString(sn.getHashRange()),
					ns.getLastUpdated(), ns.getState()));
		});

		long[] largestRange = nodeStatistics.get(0).getServerNode().getHashRange();

		System.out.println("Largest range is " + nodeStatistics.get(0).getServerNode());

		long largestSpace = nodeStatistics.get(0).getIndexSpace();
		System.out.println("Largest space is " + largestSpace);
		long[] revised = new long[] { largestRange[0], (long) Math.ceil(largestRange[1] - (largestSpace / 2)) };
		long[] newRange = new long[] { (long) (largestRange[0] + Math.ceil(largestSpace / 2)), largestRange[1] };

		System.out.println("Revised Range space is " + Arrays.toString(revised));
		System.out.println("New Range space is " + Arrays.toString(newRange));

		ServerNode updated = nodeStatistics.get(0).getServerNode();
		updated.setHashRange(revised);
		Cluster.membershipList.addUpdate(updated);

		System.err.println("Setting revised range to " + nodeStatistics.get(0).getServerNode());
		serverNode.setHashRange(newRange);
		serverNode.setAllocated(true);
		System.err.println("Setting new range to " + serverNode);
		Cluster.membershipList.addUpdate(serverNode);
		
		new GossipWorker().broadcast();
		return serverNode;
	}

	public static void main(String[] args) {

		MembershipList masterListex = new MembershipList();
		Cluster.updated = 6;
		Cluster.membershipList = masterListex;
		masterListex.setUpdated(10);
		ServerNode serverNode = new ServerNode("localhost", 9090);
		serverNode.setMaster(true);
		Cluster.me = serverNode;

		ServerNode sn1 = allocate(serverNode);
		Node n1 = new Node(sn1);
		masterListex.add(n1);

		ServerNode sn2 = allocate(new ServerNode("localhost", 8080));
		Node n2 = new Node(sn2);

		ServerNode sn3 = allocate(new ServerNode("localhost", 7070));
		Node n3 = new Node(sn3);

		ServerNode sn4 = allocate(new ServerNode("localhost", 6060));
		Node n4 = new Node(sn4);

		masterListex.getNodes().values().forEach(ns -> {
			ServerNode sn = ns.getServerNode();
			System.err.println(String.format("%10s,%10s,%10s,%10s", sn.getId(), Arrays.toString(sn.getHashRange()),
					ns.getLastUpdated(), ns.getState()));
		});

	}
}
