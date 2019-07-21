package com.esphere.iceberg.core.client;

import java.util.Map;

import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;

public class IBInstance {
	
	private static String host = "localhost";
	private static int port = 9090;
	
	private static NIOClient client = new NIOClient(host, port);

	public static IBInstance newIBInstance() {
		client.send(new Event(EventType.GET_META_DATA, null));
		return new IBInstance();
	}

	public IBMap<String, Object> getMap(String string) {
		return new IBMap<>(string);
	}

}
