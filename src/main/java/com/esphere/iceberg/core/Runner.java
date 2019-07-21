package com.esphere.iceberg.core;

import org.apache.commons.lang3.StringUtils;

import com.esphere.iceberg.support.ServerNode;

public class Runner {

	static Cluster cluster = new Cluster();

	public static void main(String[] args) {
		int port = 9090;
		String master = "";
		boolean isMaster = true;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}

		if (args.length > 1) {
			isMaster = Boolean.parseBoolean(args[1]);
		}
		if (args.length > 2) {
			master = args[2];
		}

		ServerNode serverNode = new ServerNode("localhost", port);
		serverNode.setAlive(true);
		serverNode.setMaster(isMaster);
		serverNode.setId(Cluster.id);
		Cluster cluster = new Cluster().withReplicationFactor(1).me(serverNode);
		if (StringUtils.isNotEmpty(master)) {
			cluster.withServerNode(new ServerNode(master.split(":")[0], Integer.parseInt(master.split(":")[1])));
		}
		cluster.provision();

	}
}
