package com.esphere.iceberg.workers;

import java.util.Iterator;
import java.util.concurrent.Callable;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.core.ClusterManager;
import com.esphere.iceberg.support.Node;

public class RebalanceWorker implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		while (true) {
			try {
				Thread.sleep(10000);
				Iterator<Node> iterator = Cluster.membershipList.getNodes().values().iterator();
				while (iterator.hasNext()) {
					Node ns = iterator.next();
					if (ns.isAlive() == false && ns.isAllocated() == true) {
						System.out.println(String.format("Node %s is down", ns));
						ClusterManager.repair(ns);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}

}
