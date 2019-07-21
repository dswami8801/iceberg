package com.esphere.iceberg.network;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.esphere.iceberg.handlers.DataEventHandler;
import com.esphere.iceberg.handlers.EventHandler;
import com.esphere.iceberg.handlers.GetEventHandler;
import com.esphere.iceberg.handlers.GossipEventHandler;
import com.esphere.iceberg.handlers.HeartBeatEventHndler;
import com.esphere.iceberg.handlers.MetadataEventHandler;
import com.esphere.iceberg.handlers.RegisterEventHandler;
import com.esphere.iceberg.handlers.ReplicaEventHandler;
import com.esphere.iceberg.handlers.RepositoryEventHandler;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;

public class RequestHandler implements Callable<Object> {

	private static Logger LOGGER = Logger.getLogger(RequestHandler.class);

	private static Map<EventType, EventHandler> eventHandlers;

	static {
		eventHandlers = new HashMap<>();
		eventHandlers.put(EventType.HEART_BEAT, new HeartBeatEventHndler());
		eventHandlers.put(EventType.MASTER_LIST_UPDATE, new GossipEventHandler());
		eventHandlers.put(EventType.PUT_DATA, new DataEventHandler());
		eventHandlers.put(EventType.GET_DATA, new GetEventHandler());
		eventHandlers.put(EventType.WRITE_REPLICA, new ReplicaEventHandler());
		eventHandlers.put(EventType.GET_META_DATA, new MetadataEventHandler());
		eventHandlers.put(EventType.GET_REPO_META_DATA, new RepositoryEventHandler());
		eventHandlers.put(EventType.HASH_ALLOCATION, new RegisterEventHandler());
	}

	public Event doHandle(Event event) throws Exception {
		Event response = null;
		try {
			EventHandler eventHandler = eventHandlers.get(event.getType());
			response = eventHandler.handle(event);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
		}
		return response;

	}

	@Override
	public Object call() throws Exception {
		return null;
	}

}
