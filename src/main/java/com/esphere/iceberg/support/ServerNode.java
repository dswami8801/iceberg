package com.esphere.iceberg.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ServerNode implements Serializable {

	private static final long serialVersionUID = -717109353288451339L;
	private String id;
	private String host;
	private int port;
	private boolean alive;
	private long[] hashRange = new long[] { -1, -1 };
	private boolean isMaster;
	private boolean isAllocated;
	@JsonIgnore
	private List<Replica> replicas = new ArrayList<>();

	public ServerNode() {
	}

	public ServerNode(String host, int port) {
		this.host = host;
		this.port = port;
		this.id = host + ":" + port;
	}

	public String getId() {
		return host + ":" + port;
	}

	public void setId(String id) {
		this.id = host + ":" + port;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public long[] getHashRange() {
		return hashRange;
	}

	public void setHashRange(long[] hashRange) {
		this.hashRange = hashRange;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<Replica> getReplicas() {
		return replicas;
	}

	public void setReplicas(List<Replica> replicas) {
		this.replicas = replicas;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public boolean isAllocated() {
		return isAllocated;
	}

	public void setAllocated(boolean isAllocated) {
		this.isAllocated = isAllocated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + port;
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
		ServerNode other = (ServerNode) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getId();
	}

	

}
