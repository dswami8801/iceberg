package com.esphere.iceberg.core.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.esphere.iceberg.core.Cluster;
import com.esphere.iceberg.payloads.DataPayload;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.GossipPayload;
import com.esphere.iceberg.support.Record;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

public class ClientEventHandler extends ChannelInboundMessageHandlerAdapter<Event> {

	private static final Map<String, RecordFuture<Record>> map = new ConcurrentHashMap<>();

	@Override
	public void messageReceived(ChannelHandlerContext channelHandlerContext, Event s) throws Exception {

		Event event = (Event) s;

		if (event.getType().equals(EventType.GET_DATA)) {
			DataPayload dataPayload = (DataPayload) event.getPayload();
			System.out.println("Received Record : " + dataPayload.getContent());
			RecordFuture<Record> future = map.get(event.getEventId());
			future.setRecord(dataPayload.getContent().get(0));
			future.setDone(new AtomicBoolean(true));
		}

		if (event.getType().equals(EventType.GET_META_DATA)) {
			GossipPayload gossipPayload = (GossipPayload) event.getPayload();
			Cluster.prettyPrint(gossipPayload.getMasterList());
			Members.MEMBERS.update(gossipPayload.getMasterList().getNodes().values());
			System.out.println("Received Metadata : " + gossipPayload.masterList);
		}

	}

	public void setFuture(RecordFuture<Record> future) {
		map.put(future.getRequestId(), future);
	}

}
