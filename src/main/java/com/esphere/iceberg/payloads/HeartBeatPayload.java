package com.esphere.iceberg.payloads;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HeartBeatPayload extends Payload {

	private static final long serialVersionUID = -3597536146676598744L;

	@Override
	public Boolean getContent() {
		return true;
	}

	@Override
	public String toString() {
		return "HeartBeatPayload";
	}

	public static void main(String[] args) throws UnknownHostException {
		System.out.println(InetAddress.getLocalHost());
	}
}
