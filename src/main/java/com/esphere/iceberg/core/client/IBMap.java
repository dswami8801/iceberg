package com.esphere.iceberg.core.client;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.esphere.iceberg.support.Record;

public class IBMap<K, V> implements Map<K, V> {

	private MapContainer<K,V> mapContainer;
	

	public IBMap(String bucketKey) {
		super();
		this.mapContainer = new MapContainer<K,V>(bucketKey);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public V get(Object key) {
		try {
			return (V) mapContainer.getItem(key).get().getData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}
	
	public Future<Record> getAsync(Object key) {
		try {
			return mapContainer.getItem(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}

	@Override
	public V put(K key, V value) {
		return mapContainer.putItem(key, value);
	}

	@Override
	public V remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {

	}

	@Override
	public void clear() {

	}

	@Override
	public Set<K> keySet() {
		return null;
	}

	@Override
	public Collection<V> values() {
		return null;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return null;
	}

}
