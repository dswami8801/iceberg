package com.esphere.iceberg.network;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class RequestDelegator {

	private static Logger LOGGER = Logger.getLogger(RequestDelegator.class);

	private ExecutorService executorService;

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public RequestDelegator(Integer capacity) {
		executorService = Executors.newFixedThreadPool(capacity);
	}

	public void delegate(Socket socket) {

		//executorService.submit(new RequestHandler(socket));

	}

}
