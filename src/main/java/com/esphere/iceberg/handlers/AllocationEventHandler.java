package com.esphere.iceberg.handlers;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.ServerPayload;

public class AllocationEventHandler implements EventHandler {

	@Override
	public Event handle(final Event event) {
		ServerPayload serverPayload = (ServerPayload) event.getPayload();
		System.out.println("Allocated " + serverPayload.getServerNode());
		Cluster.me.setAllocated(true);
		Cluster.me.setHashRange(serverPayload.getServerNode().getHashRange());
		return event;
	}

}
