package com.esphere.iceberg.payloads;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.support.MembershipList;

public class GossipPayload extends Payload {

	private static final long serialVersionUID = 1513856475650054538L;

	public MembershipList masterList = Cluster.membershipList;

	@Override
	public MembershipList getContent() {
		return masterList;
	}

	public MembershipList getMasterList() {
		return masterList;
	}

	public void setMasterList(MembershipList masterList) {
		this.masterList = masterList;
	}

}
