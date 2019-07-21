package com.esphere.iceberg.payloads;

import java.util.ArrayList;
import java.util.List;

import com.esphere.iceberg.support.Record;

public class DataPayload extends Payload {

	private static final long serialVersionUID = -4843197778970749278L;

	public List<Record> records = new ArrayList<>();

	public DataPayload() {
	}

	public DataPayload(List<Record> records) {
		this.records = records;
	}

	public DataPayload(Record record) {
		this.records.add(record);
	}

	@Override
	public List<Record> getContent() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	@Override
	public String toString() {
		return "DataPayload [records=" + records + "]";
	}

}
