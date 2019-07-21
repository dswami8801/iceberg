package com.esphere.iceberg.support;

import java.io.Serializable;

import com.esphere.iceberg.util.HashFunction;

public class Record implements Serializable {

	public Object data;
	public String key;
	public long hash;
	public String bucketKey;
	public Replica replica;

	public Record(String key, Object data) {
		this.data = data;
		this.key = key;
	}
	
	

	public Record(String bucketKey, String key,Object data) {
		super();
		this.data = data;
		this.key = key;
		this.bucketKey = bucketKey;
	}



	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getHash() {
		return HashFunction.calculate(key);
	}

	public void setHash(long hash) {
		this.hash = HashFunction.calculate(key);
	}

	public String getBucketKey() {
		return bucketKey;
	}

	public void setBucketKey(String bucketKey) {
		this.bucketKey = bucketKey;
	}

	public Replica getReplica() {
		return replica;
	}

	public void setReplica(Replica replica) {
		this.replica = replica;
	}



	@Override
	public String toString() {
		return "Record [data=" + data + ", key=" + key + ", bucketKey=" + bucketKey + "]";
	}

	

}
