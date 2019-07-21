package com.esphere.iceberg.payloads;

import java.io.Serializable;
import java.util.UUID;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.support.ServerNode;

public class Event implements Serializable {

	private static final long serialVersionUID = -8463254878114369489L;

	public String eventId = UUID.randomUUID().toString();
	public ServerNode serverNode;
	public EventType type;
	public Payload payload;

	public Event() {
	}

	public Event(EventType type, Payload payload) {
		this.type = type;
		this.payload = payload;
		this.serverNode = Cluster.me;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Payload getPayload() {
		return payload;
	}

	public ServerNode getServerNode() {
		return serverNode;
	}

	public void setServerNode(ServerNode serverNode) {
		this.serverNode = serverNode;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "Event [serverNode=" + serverNode + ", type=" + type + ", payload=" + payload + "]";
	}

	

}
