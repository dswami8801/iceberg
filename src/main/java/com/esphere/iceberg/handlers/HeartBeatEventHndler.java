package com.esphere.iceberg.handlers;

import java.util.Date;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.HeartBeatPayload;
import com.esphere.iceberg.payloads.OkPayload;
import com.esphere.iceberg.support.MembershipList;
import com.esphere.iceberg.support.Node;

public class HeartBeatEventHndler implements EventHandler {

	@Override
	public Event handle(Event event) {
		HeartBeatPayload heartBeatPayload = (HeartBeatPayload) event.getPayload();
		MembershipList masterList = Cluster.membershipList;
		Node target = null;
		for (Node ns : masterList.getNodes().values()) {
			if (ns.getServerNode().equals(event.getServerNode())) {
				target = ns;
				target.increaseHeartbeatCount();
				target.getServerNode().setAlive(true);
				target.setLastUpdated(new Date().getTime());
				target.getServerNode().setAllocated(true);
				target.setState("AVAILABLE");
				break;
			}
		}

		if (target == null) {
			// New Node joined
			masterList.add(new Node(event.getServerNode()));
		}
		return OkPayload.ok();
	}

}
