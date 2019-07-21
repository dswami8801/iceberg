package com.esphere.iceberg.support;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ReplicaSet {

	private ServerNode leader;
	private Set<ServerNode> followers = new HashSet<>();

	public ServerNode getLeader() {
		return leader;
	}

	public void setLeader(ServerNode leader) {
		this.leader = leader;
	}

	public Set<ServerNode> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<ServerNode> followers) {
		this.followers = followers;
	}

	@Override
	public String toString() {
		return "ReplicaSet [leader=" + leader.getId() + ", followers=" + followers.stream().map(f->f.getId()).collect(Collectors.toList()) + "]";
	}

}
