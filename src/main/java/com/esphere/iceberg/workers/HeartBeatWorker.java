package com.esphere.iceberg.workers;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Callable;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.network.netty.NIOClient;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.HeartBeatPayload;
import com.esphere.iceberg.support.Node;
import com.esphere.iceberg.support.ServerNode;

public class HeartBeatWorker implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		while (true) {
			try {
				Iterator<Node> iterator = Cluster.membershipList.getNodes().values().iterator();
				while (iterator.hasNext()) {
					ServerNode serverNode = iterator.next().getServerNode();
					if (!(serverNode.getPort() == Cluster.me().getPort()) && serverNode.isAlive())
						HeartBeatSender.advertise(serverNode);
				}
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
			}
		}
	}

}

class HeartBeatSender {

	public static void advertise(ServerNode serverNode) {
		NIOClient client = new NIOClient(serverNode);
		try {
			client.openConnection();
			client.send(new Event(EventType.HEART_BEAT, new HeartBeatPayload()));
			client.closeConnection();
		} catch (IOException e) {
			System.err.println("Host not reachable" + serverNode.getId());
		} finally {
			client.closeConnection();
		}
	}
}