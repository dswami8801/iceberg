package com.esphere.iceberg.payloads;

import java.util.ArrayList;
import java.util.List;

import com.esphere.iceberg.support.Record;

public class ReplicaPayload extends Payload {

	private static final long serialVersionUID = -6938658507722537489L;

	public List<Record> records = new ArrayList<>();

	public ReplicaPayload(List<Record> records) {
		this.records=records;
	}

	public ReplicaPayload() {
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	@Override
	public Object getContent() {
		return records;
	}

}
