package com.esphere.iceberg.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.esphere.iceberg.support.Record;

public enum DataRepository {

	DATA;

	private static final Map<String, Map<String, Record>> repository = new ConcurrentHashMap<>();

	public static Map<String, Integer> getRepository() {
		Map<String, Integer> meta = new HashMap<>();
		repository.entrySet().forEach(e -> {
			meta.put(e.getKey(), e.getValue().size());
		});

		return meta;
	}

	public Record get(String key, String objectKey) {
		Map<String, Record> data = repository.get(key);
		if (data != null) {
			return data.get(objectKey);
		}
		return null;
	}

	public Record add(String key, Record record) {
		Map<String, Record> data = repository.get(key);
		if (data == null) {
			repository.put(key, new ConcurrentHashMap<>());
		}
		return repository.get(key).put(record.getKey(), record);
	}

	public static void main(String[] args) {
		DataRepository.DATA.add("city", new Record("111", "1111"));
	}
}
