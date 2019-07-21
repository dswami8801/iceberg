package com.esphere.iceberg.handlers;

import com.esphere.iceberg.payloads.Event;

public interface EventHandler {
	
	public Event handle(Event event);

}
