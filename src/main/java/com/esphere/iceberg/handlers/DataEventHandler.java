package com.esphere.iceberg.handlers;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.payloads.DataPayload;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.OkPayload;

public class DataEventHandler implements EventHandler {

	@Override
	public Event handle(Event event) {
		
		DataPayload dataPayload = (DataPayload) event.getPayload();
		System.out.println("Received data put request "+dataPayload.getContent().size());
		dataPayload.getContent().forEach(System.out::println);
		Cluster.put(dataPayload.getContent());
		return OkPayload.ok();
	}

}
