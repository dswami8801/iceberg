package com.esphere.iceberg.core.client;

public class Member {

	private String id;
	private String host;
	private int port;
	private boolean alive;
	private long[] hashRange = new long[] { -1, -1 };
	private boolean isMaster;
	private boolean isAllocated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		Member other = (Member) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
