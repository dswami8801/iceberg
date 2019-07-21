package com.esphere.iceberg.handlers;

import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.GossipPayload;

public class MetadataEventHandler implements EventHandler {

	@Override
	public Event handle(Event event) {

		return new Event(EventType.GET_META_DATA, new GossipPayload());
	}

}
