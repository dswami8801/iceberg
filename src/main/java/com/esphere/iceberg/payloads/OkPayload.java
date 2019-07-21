package com.esphere.iceberg.payloads;

public class OkPayload extends Payload {

	private static final long serialVersionUID = 36064017574203995L;

	@Override
	public Object getContent() {
		return "OK";
	}
	
	public static Event ok() {
		return new Event(EventType.OK, new OkPayload());
	}

}
