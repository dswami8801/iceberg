package com.esphere.iceberg.handlers;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.GossipPayload;
import com.esphere.iceberg.payloads.OkPayload;
import com.esphere.iceberg.support.MembershipList;

public class GossipEventHandler implements EventHandler {

	@Override
	public Event handle(Event event) {
		GossipPayload gossipPayload = (GossipPayload) event.getPayload();
		// Update master list of this node only if it's latest
		MembershipList masterList = Cluster.membershipList;
		// if (!Cluster.me.isMaster())

		masterList.update(gossipPayload.getMasterList());
		Cluster.updated = Cluster.updated + 1;

		return OkPayload.ok();
	}

}
