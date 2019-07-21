package com.esphere.iceberg.core.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.esphere.iceberg.support.Record;

public class IBClient {

	public static void main(String[] args) throws InterruptedException {
		IBInstance ibInstance = IBInstance.newIBInstance();
		IBMap<String, Object> map = ibInstance.getMap("users");
//		for (int i = 0; i < 1000; i++) {
//			map.put("" + i, "user " + i);
//			try {
//				//TimeUnit.MILLISECONDS.sleep(10);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}
		List<Future<Record>> futures = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			futures.add(map.getAsync("" + i));
			System.out.println(i);
		}
		TimeUnit.SECONDS.sleep(10);
		futures.forEach(f -> {
			try {
				System.err.println(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
//		System.exit(0);
	}

}
