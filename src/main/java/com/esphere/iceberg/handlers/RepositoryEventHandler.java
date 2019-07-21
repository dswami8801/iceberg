package com.esphere.iceberg.handlers;

import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.persistence.DataRepository;

public class RepositoryEventHandler implements EventHandler {

	@Override
	public Event handle(Event event) {
		System.out.println(DataRepository.getRepository());
		return event;
	}

}
