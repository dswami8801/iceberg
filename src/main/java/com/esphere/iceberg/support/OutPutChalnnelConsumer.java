package com.esphere.iceberg.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.esphere.iceberg.network.netty.NIOClient;
import com.esphere.iceberg.payloads.DataPayload;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.payloads.ReplicaPayload;

public class OutPutChalnnelConsumer implements Runnable {

	@Override
	public void run() {
		while (true) {
			OutputChannel.getKeys().forEach(key -> {

				BlockingQueue<RecordWrapper> queue = OutputChannel.getQueue(key);
				List<RecordWrapper> records = new ArrayList<>();
				try {
					int limit = 0;
					while (!queue.isEmpty() && limit < 1000) {
						RecordWrapper rd = queue.take();
						records.add(rd);
						limit++;
					}
					flush(key, records);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			sleep();
		}

	}

	private void sleep() {
		try {
			TimeUnit.MICROSECONDS.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void flush(ServerNode key, List<RecordWrapper> records) {
		if (records.size() == 0)
			return;
		Map<EventType, List<RecordWrapper>> map = records.stream()
				.collect(Collectors.groupingBy(RecordWrapper::getEventType));
		NIOClient client = new NIOClient(key);
		
		List<RecordWrapper> putRequestWrapper = map.get(EventType.PUT_DATA);
		if(putRequestWrapper!=null) {
			List<Record> putRecords = putRequestWrapper.stream().map(rw -> rw.getRecord()).collect(Collectors.toList());
			client.send(new Event(EventType.PUT_DATA, new DataPayload(putRecords)));
			System.out.println(String.format("Flushed %d put records to %s", putRecords.size(), key));
		}
		
		
		List<RecordWrapper> replicaRequestWrapper = map.get(EventType.WRITE_REPLICA);
		if(replicaRequestWrapper!=null) {
		List<Record> replicaRecords = replicaRequestWrapper.stream().map(rw -> rw.getRecord())
				.collect(Collectors.toList());
		client.send(new Event(EventType.WRITE_REPLICA, new ReplicaPayload(replicaRecords)));
		System.out.println(String.format("Flushed %d replica records to %s", replicaRecords.size(), key));
		}
		
		
		return;

	}

	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					OutputChannel.main(null);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
		new Thread(new OutPutChalnnelConsumer()).start();
	}

}
