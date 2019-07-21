package com.esphere.iceberg.core.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.esphere.iceberg.payloads.DataPayload;
import com.esphere.iceberg.payloads.Event;
import com.esphere.iceberg.payloads.EventType;
import com.esphere.iceberg.support.Node;
import com.esphere.iceberg.support.OutputChannel;
import com.esphere.iceberg.support.Record;

public class OutPutChalnnelConsumer implements Runnable {

	@Override
	public void run() {
		while (true) {
			RequestContainer.getKeys().forEach(key -> {

				BlockingQueue<Record> queue = RequestContainer.getQueue(key);
				List<Record> records = new ArrayList<>();
				try {
					int limit = 0;
					while (!queue.isEmpty() && limit < 1000) {
						Record rd = queue.take();
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
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void flush(Node key, List<Record> records) {
		if (records.size() == 0)
			return;
		NIOClient client = new NIOClient(key.getServerNode());
		client.send(new Event(EventType.PUT_DATA, new DataPayload(records)));

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
