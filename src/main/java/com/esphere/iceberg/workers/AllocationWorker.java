package com.esphere.iceberg.workers;

import java.io.IOException;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.network.netty.NIOClient;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.ServerPayload;
import com.esphere.iceberg.support.ServerNode;

public class AllocationWorker {

	public void advertise() {
		//Cluster.membershipList.add(getMetaData());
		ServerNode master = Cluster.membershipList.getNodes().values().stream()
				.filter(ns -> ns.getServerNode().isMaster()).findFirst().get().getServerNode();
		NIOClient client = new NIOClient(master);
		try {
			client.openConnection();
			client.send(new Event(EventType.HASH_ALLOCATION, null));
//			ServerPayload serverPayload = (ServerPayload) event.getPayload();
//			System.out.println("Allocated " + serverPayload.getServerNode());
//			Cluster.me.setAllocated(true);
//			Cluster.me.setHashRange(serverPayload.getServerNode().getHashRange());
			client.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public Node getMetaData() {
//		TcpClient tcpClient = new TcpClient("localhost", 9090);
//		
//		try {
//			tcpClient.openConnection();
//			tcpClient.send(new Event(EventType.GET_META_DATA, null));
//			tcpClient.closeConnection();
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//		GossipPayload gossipPayload = (GossipPayload) event.getPayload();
//		return gossipPayload.masterList.getNodes().values().stream()
//				.filter(ns -> ns.getServerNode().isMaster()).findFirst().get();
//	}

}
