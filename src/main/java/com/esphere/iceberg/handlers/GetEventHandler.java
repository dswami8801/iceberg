package com.esphere.iceberg.handlers;

import java.util.concurrent.TimeUnit;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.payloads.DataPayload;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.support.Record;

public class GetEventHandler implements EventHandler {

	@Override
	public Event handle(Event event) {
		DataPayload dataPayload = (DataPayload) event.getPayload();
		Record record = dataPayload.getContent().get(0);
		Record r =  Cluster.get(record.getBucketKey(), record.getKey());
		System.out.println("Returning data :"+r);
		Event response = new Event(EventType.GET_DATA,new DataPayload(r));
		response.setEventId(event.getEventId());
		return response;
	}

}
