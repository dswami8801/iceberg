package com.esphere.iceberg.core.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.esphere.iceberg.support.Node;

public enum Members {

	MEMBERS;
	private Set<Node> memberSet = new HashSet<>();

	public void update(Collection<Node> collection) {
		memberSet.addAll(collection);
	}

	public Set<Node> getMemberSet() {
		while (memberSet.size() == 0) {
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return memberSet;
	}

}
