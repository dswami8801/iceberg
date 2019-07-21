package com.esphere.iceberg.support;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Replica implements Serializable {

	private static final long serialVersionUID = 815548135600765018L;
	private String key;
	private long sequenceNo;
	private String type;
	private ServerNode leader;
	private Set<ServerNode> followers = new HashSet<>();
	private String status = "READY";
	
	public Replica() {
	}

	public Replica(String key) {
		super();
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		Replica other = (Replica) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
