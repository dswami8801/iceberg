package com.esphere.iceberg.support;

import com.esphere.iceberg.payloads.EventType;

public class RecordWrapper {

	private ServerNode key;
	private Record record;
	private ReplicaSet replicaSet;
	private EventType eventType;
	private Replica replica;

	public RecordWrapper(ServerNode key, Record record, ReplicaSet replicaSet, EventType type) {
		super();
		this.key = key;
		this.record = record;
		this.replicaSet = replicaSet;
		this.eventType = type;
	}

	public ServerNode getKey() {
		return key;
	}

	public void setKey(ServerNode key) {
		this.key = key;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public ReplicaSet getReplicaSet() {
		return replicaSet;
	}

	public void setReplicaSet(ReplicaSet replicaSet) {
		this.replicaSet = replicaSet;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Replica getReplica() {
		return replica;
	}

	public void setReplica(Replica replica) {
		this.replica = replica;
	}

}
