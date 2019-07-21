package com.esphere.iceberg.core.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecordFuture<T> implements Future<T> {

	private String requestId;
	private T record;
	private AtomicBoolean isDone = new AtomicBoolean(false);
	private CountDownLatch latch = new CountDownLatch(1);

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return isDone.get();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {

		latch.await();
		return record;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		latch.await(timeout, unit);
		return record;
	}

	public void setRecord(T record) {
		this.record = record;

	}

	public void setDone(AtomicBoolean isDone) {
		this.isDone = isDone;
		if (latch.getCount() == 0) {
			System.out.println("Repeated operation");
			throw new IllegalStateException("Repeated operation");
		}
		latch.countDown();

	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "RecordFuture [record=" + record + ", isDone=" + isDone + "]";
	}

}
