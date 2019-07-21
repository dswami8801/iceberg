package com.esphere.iceberg.workers;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Callable;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.network.netty.NIOClient;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.GossipPayload;
import com.esphere.iceberg.support.Node;
import com.esphere.iceberg.support.ServerNode;

public class GossipWorker implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		while (true) {
			try {
				Thread.sleep(5000);
				broadcast();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void broadcast() {
		try {
			Iterator<Node> iterator = Cluster.membershipList.getNodes().values().iterator();
			while (iterator.hasNext()) {
				Node nodeStatistics = iterator.next();
				ServerNode serverNode = nodeStatistics.getServerNode();
				if (serverNode.isAlive())
					MasterListSender.advertise(serverNode);
				if (nodeStatistics.getFailedThreshold() > 5 && !nodeStatistics.isAllocated())
					Cluster.membershipList.getNodes().remove(serverNode.getId());
			}

		} finally {
		}

	}

}

class MasterListSender {

	public static void advertise(ServerNode serverNode) {
		NIOClient client = new NIOClient(serverNode);
		try {
			client.openConnection();
			GossipPayload gossipPayload = new GossipPayload();
			client.send(new Event(EventType.MASTER_LIST_UPDATE, gossipPayload));
			client.closeConnection();
		} catch (IOException e) {
			System.err.println("Host not reachable" + serverNode.getId());
		} finally {
			client.closeConnection();
		}
	}
}