package com.esphere.iceberg.payloads;

import com.esphere.iceberg.support.ServerNode;

public class ServerPayload extends Payload {

	private static final long serialVersionUID = 5189205698507784377L;

	private ServerNode serverNode;

	public ServerPayload(ServerNode serverNode) {
		super();
		this.serverNode = serverNode;
	}

	@Override
	public Object getContent() {
		return serverNode;
	}

	public ServerNode getServerNode() {
		return serverNode;
	}

	public void setServerNode(ServerNode serverNode) {
		this.serverNode = serverNode;
	}

}
