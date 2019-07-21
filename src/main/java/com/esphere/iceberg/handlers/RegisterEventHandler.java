package com.esphere.iceberg.handlers;

import com.esphere.iceberg.core.ClusterManager;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.ServerPayload;

public class RegisterEventHandler implements EventHandler {

	@Override
	public Event handle(final Event event) {
		return new Event(EventType.HASH_ALLOCATED, new ServerPayload(ClusterManager.allocate(event.getServerNode())));
	}

}
