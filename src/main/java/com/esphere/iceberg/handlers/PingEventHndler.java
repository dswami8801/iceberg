package com.esphere.iceberg.handlers;

import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.OkPayload;

public class PingEventHndler implements EventHandler {

	@Override
	public Event handle(Event event) {
		return OkPayload.ok();//new Event(EventType.PING, new OkPayload());
		
	}

}
