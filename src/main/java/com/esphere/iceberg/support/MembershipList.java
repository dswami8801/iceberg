package com.esphere.iceberg.support;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.esphere.iceberg.core.Cluster;

public class MembershipList implements Serializable {

	private static final long serialVersionUID = -3604367002494015970L;
	private Map<String, Node> nodes = new ConcurrentHashMap<>();
	private long updated;
	private long count;

	public Map<String, Node> getNodes() {
		return nodes;
	}

	public void setNodes(Map<String, Node> nodeStaticstics) {
		this.nodes = nodeStaticstics;
	}

	public void add(Node nodeStaticstics) {
		this.nodes.put(nodeStaticstics.getId(), nodeStaticstics);
	}

	public void addAll(List<Node> nodeStaticstics) {
		nodeStaticstics.forEach(ns -> {
			add(ns);
		});
	}

	public void update(MembershipList masterList) {
		count++;
		Map<String, Node> inComing = masterList.getNodes();

		Iterator<Node> iterator = nodes.values().iterator();
		while (iterator.hasNext()) {
			Node nodeStatistics = iterator.next();
			Node inComingNodeStstistics = masterList.getNodes().get(nodeStatistics.getId());
			if (nodeStatistics != null && inComingNodeStstistics != null) {
				if ((new Date().getTime() - nodeStatistics.getLastUpdated() > 10 * 1000)
						&& !inComingNodeStstistics.getServerNode().equals(Cluster.me())) {
					nodeStatistics.setState("UNAVAILABLE");
					nodeStatistics.setFailedThreshold(nodeStatistics.getFailedThreshold() + 1);
					nodeStatistics.getServerNode().setAlive(false);
					System.out.println(
							String.format("Marking %s as unavailable", nodeStatistics.getServerNode().getId()));
				}
				if (inComingNodeStstistics.getHeartbeatCount() > nodeStatistics.getHeartbeatCount()) {
					nodeStatistics.setHeartbeatCount(inComingNodeStstistics.getHeartbeatCount());
					nodeStatistics.setFailedThreshold(inComingNodeStstistics.getFailedThreshold());
					nodeStatistics.setState(nodeStatistics.getState());
					nodeStatistics.getServerNode().setAlive(true);
					nodeStatistics.setLastUpdated(new Date().getTime());
					nodeStatistics.setHashRange(inComingNodeStstistics.getHashRange());

				}

				if (inComingNodeStstistics.getHeartbeatCount() < 0) {
					System.out.println("Node eligible to remove");
					ServerNode node = nodeStatistics.getServerNode();
					node.setAllocated(false);
					node.setAlive(false);
					node.setHashRange(new long[] { -1, -1 });
					nodeStatistics.setFailedThreshold(nodeStatistics.getFailedThreshold() + 1);
					nodeStatistics.setSelectiveIndex(0);
					nodeStatistics.setHeartbeatCount(inComingNodeStstistics.getHeartbeatCount());
					nodeStatistics.setState("DEAD");
				}
				inComing.remove(inComingNodeStstistics.getId());
			}
		}
		inComing.entrySet().forEach(ic -> {
			if (ic.getValue().isAlive()) {
				Node nodeStatistics = ic.getValue();
				nodeStatistics.setLastUpdated(new Date().getTime());
				add(nodeStatistics);
			}
		});
		if (count % 5 == 0)
			Cluster.prettyPrint();
	}

	public boolean isUpdated() {
		return updated > 5;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "MasterList [nodeStaticstics=" + nodes + ", updated=" + updated + "]";
	}

	public void addUpdate(ServerNode serverNode) {
		Node nodeStatistics = nodes.get(serverNode.getId());
		if (nodeStatistics != null) {
			nodeStatistics.setServerNode(serverNode);
			nodeStatistics.setHeartbeatCount(nodeStatistics.getHeartbeatCount() + 10);
		} else {
			Node statistics = new Node(serverNode);
			statistics.setHeartbeatCount(statistics.getHeartbeatCount() + 10);
			statistics.setLastUpdated(new Date().getTime());
			statistics.setState("AVAILABLE");
			add(statistics);
		}
	}

}
