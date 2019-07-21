//package com.esphere.iceberg.network;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SocketChannel;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import com.esphere.iceberg.payloads.DataPayload;
//import com.esphere.iceberg.payloads.Event;
//import com.esphere.iceberg.payloads.EventType;
//import com.esphere.iceberg.payloads.GossipPayload;
//import com.esphere.iceberg.support.Node;
//import com.esphere.iceberg.support.Record;
//import com.esphere.iceberg.support.Replica;
//import com.esphere.iceberg.support.ServerNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class TcpClientXX {
//
//	SocketChannel channel = null;
//
//	public TcpClientXX(String host, int port) {
//		InetSocketAddress hostAddress = new InetSocketAddress(host, port);
//		try {
//			channel = SocketChannel.open(hostAddress);
//			if (channel == null) {
//				throw new IllegalStateException("Unable to acquire connection with server");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public TcpClientXX(ServerNode serverNode) {
//		this(serverNode.getHost(), serverNode.getPort());
//	}
//
//	public void openConnection() throws IOException {
//
//	}
//
//	public void send(Object msg) throws IOException {
//		try {
//			channel.write(serialize(msg));
//			ByteBuffer res = ByteBuffer.allocate(100);
//			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//			while (channel.read(res) > 0) {
//				arrayOutputStream.write(res.array());
//				res.clear();
//
//			}
//			channel.close();
//			deSerialize(ByteBuffer.wrap(arrayOutputStream.toByteArray()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public void closeConnection() throws IOException {
//
//	}
//
//	public ByteBuffer serialize(Object payload) {
//		byte[] data = null;
//		try {
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			ObjectOutputStream oos = new ObjectOutputStream(bos);
//			oos.writeObject(payload);
//			oos.flush();
//			data = bos.toByteArray();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ByteBuffer.wrap(data);
//	}
//
//	public Object deSerialize(ByteBuffer byteBuffer) {
//		Object object = null;
//		try {
//			ByteArrayInputStream bos = new ByteArrayInputStream(byteBuffer.array());
//			ObjectInputStream oos = new ObjectInputStream(bos);
//			object = oos.readObject();
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return object;
//	}
//
//	public static void main(String[] args) throws Exception {
//
//		put();
//		getMetaData();
////		get();
//	}
//
//	public static void put() throws Exception {
//		for (int j = 0; j < 10; j++) {
//			List<Record> records = new ArrayList<>();
//			Event event = new Event();
//			event.setType(EventType.PUT_DATA);
//			DataPayload dataPayload1 = new DataPayload("city-" + j);
//			for (int i = 0; i < 1000; i++) {
//				records.add(new Record(Integer.toString(i), "Mumbai" + i));
//			}
//			dataPayload1.setRecords(records);
//			event.setPayload(dataPayload1);
//			put(event);
//		}
//
//		TimeUnit.SECONDS.sleep(1);
//
//	}
//
//	public static void put(Event event) throws IOException {
//		TcpClientXX tcpClient = new TcpClientXX("localhost", 9090);
//		tcpClient.openConnection();
//		tcpClient.send(event);
//		tcpClient.closeConnection();
//	}
//
//	public static void get() throws IOException {
//
//		List<ServerNode> node = getMetaData("continent");
//		node.forEach(n -> {
//			try {
//				hit(n);
//
//			} catch (IOException e) {
//				System.err.println("Retrying");
//			}
//		});
//
//	}
//
//	private static void hit(ServerNode n) throws IOException {
//		TcpClientXX tcpClient = new TcpClientXX(n.getHost(), n.getPort());
//		tcpClient.openConnection();
//		tcpClient.send(new Event(EventType.GET_DATA, new DataPayload("continent", new Record("99", "India"))));
//		tcpClient.closeConnection();
//
//	}
//
//	public static List<ServerNode> getMetaData(String key) throws IOException {
//		List<ServerNode> serverNodes = new ArrayList<>();
//		TcpClientXX tcpClient = new TcpClientXX("localhost", 9090);
//		tcpClient.openConnection();
//		tcpClient.send(new Event(EventType.GET_META_DATA, null));
//		tcpClient.closeConnection();
//		GossipPayload gossipPayload = null;// (GossipPayload) event.getPayload();
//		for (Node ns : gossipPayload.masterList.getNodes().values()) {
//			for (Replica r : ns.getServerNode().getReplicas()) {
//				if (r.getKey().equals(key)) {
//					serverNodes.add(r.getLeader());
//					serverNodes.addAll(r.getFollowers());
//				}
//			}
//		}
//
//		return serverNodes;
//	}
//
//	public static ServerNode getMetaData() throws IOException {
//		TcpClientXX tcpClient = new TcpClientXX("localhost", 9090);
//		tcpClient.openConnection();
//		tcpClient.send(new Event(EventType.GET_META_DATA, null));
//		tcpClient.closeConnection();
//		GossipPayload gossipPayload = null;// (GossipPayload) event.getPayload();
//		for (Node ns : gossipPayload.masterList.getNodes().values()) {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter()
//					.writeValueAsString(ns.getServerNode().getReplicas()));
//		}
//
//		TcpClientXX tcpClient2 = new TcpClientXX("localhost", 9090);
//		tcpClient2.openConnection();
//		tcpClient2.send(new Event(EventType.GET_REPO_META_DATA, null));
//		tcpClient2.closeConnection();
//
//		return null;
//	}
//}