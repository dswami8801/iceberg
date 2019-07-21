package com.esphere.iceberg.handlers;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.OkPayload;
import com.esphere.iceberg.payloads.ReplicaPayload;

public class ReplicaEventHandler implements EventHandler {

	@Override
	public Event handle(Event event) {
		
		ReplicaPayload replicaPayload = (ReplicaPayload) event.getPayload();
		System.out.println("Received replication request "+replicaPayload.getRecords().size());
		Cluster.replicate(replicaPayload);
		return OkPayload.ok();
	}

}

