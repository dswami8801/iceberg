package com.esphere.iceberg.support;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.persistence.DataRepository;

public class Node implements Serializable {

	private static final long serialVersionUID = 3147069262634295169L;
	private ServerNode serverNode;
	private long heartbeatCount = 1;
	private Long indexSpace;
	private int selectiveIndex;
	private int failedThreshold;
	private String state = "NOT_ALLOCATED";
	private transient long lastUpdated = new Date().getTime();

	public Node() {
	}

	public static void prettyPrint(MembershipList membershipList) {
//		System.out.println(
//				"-----------------------------------------------------------------------------------------------------------");
//		System.out.println(
//				String.format("%-20s|%-10s|%-10s|%-20s|%-40s", "Id", "Alive", "Allocated", "Heartbeat", "HashRange"));
//		System.out.println(
//				"-----------------------------------------------------------------------------------------------------------");
//
//		System.out.println(String.format("%-20s|%-10s|%-10s|%-20s|%-40s", serverNode.getId(), isAlive(), isAllocated(),
//				getHeartbeatCount(), Arrays.toString(serverNode.getHashRange())));
		
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
		System.out.println(String.format("%-20s|%-10s|%-10s|%-20s|%-20s|%-40s", "Key", "Type", "SeqNo", "Entries",
				"Leader", "Followers"));
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
		membershipList.getNodes().values().forEach(ns -> {
			ns.getServerNode().getReplicas().stream().forEach(r -> {
				System.out.println(String.format("%-20s|%-10s|%-10s|%-20s|%-20s|%-40s", r.getKey(), r.getType(),
						r.getSequenceNo(), DataRepository.getRepository().entrySet().stream()
								.filter(e -> e.getKey().equals(r.getKey())).collect(Collectors.toList()),
						r.getLeader(), r.getFollowers()));
			});
		});

		System.out.println(
				"-----------------------------------------------------------------------------------------------------------");
	}

	public Node(ServerNode serverNode) {
		this.serverNode = serverNode;
		long[] range = serverNode.getHashRange();
		if ((range[1] - range[0]) > 0) {
			state = "ALLOCATED";
		}
		if (serverNode.isMaster())
			selectiveIndex = Integer.MAX_VALUE;
		selectiveIndex = new Random().nextInt();

	}

	public boolean isAlive() {
		return serverNode.isAlive();
	}

	public boolean isAllocated() {
		return serverNode.isAllocated();
	}

	public ServerNode getServerNode() {
		return serverNode;
	}

	public void setServerNode(ServerNode serverNode) {
		this.serverNode = serverNode;
	}

	public long getHeartbeatCount() {
		return heartbeatCount;
	}

	public void setHeartbeatCount(long heartbeatCount) {
		this.heartbeatCount = heartbeatCount;
	}

	public void increaseHeartbeatCount() {
		this.heartbeatCount++;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public int getSelectiveIndex() {
		return selectiveIndex;
	}

	public void setSelectiveIndex(int selectiveIndex) {
		this.selectiveIndex = selectiveIndex;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getIndexSpace() {
		long[] range = serverNode.getHashRange();
		return range[1] - range[0];
	}

	public void setIndexSpace(Long indexSpace) {
		this.indexSpace = indexSpace;
	}

	public int getFailedThreshold() {
		return failedThreshold;
	}

	public void setFailedThreshold(int failedThreshold) {
		this.failedThreshold = failedThreshold;
	}

	@Override
	public String toString() {
		return "NodeStatistics [serverNode=" + serverNode + ", heartbeatCount=" + heartbeatCount + ", indexSpace="
				+ indexSpace + ", selectiveIndex=" + selectiveIndex + ", state=" + state + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serverNode == null) ? 0 : serverNode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (serverNode == null) {
			if (other.serverNode != null)
				return false;
		} else if (!serverNode.equals(other.serverNode))
			return false;
		return true;
	}

	public long[] getHashRange() {
		return serverNode.getHashRange();
	}

	public String getId() {
		return serverNode.getId();
	}

	public void setHashRange(long[] hashRange) {
		serverNode.setHashRange(hashRange);

	}

}