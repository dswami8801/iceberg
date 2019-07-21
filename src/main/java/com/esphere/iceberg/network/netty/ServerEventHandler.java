package com.esphere.iceberg.network.netty;

import com.esphere.iceberg.network.RequestHandler;
import com.esphere.iceberg.payloads.Event;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;


public class ServerEventHandler extends ChannelInboundMessageHandlerAdapter<Event> {

	RequestHandler requestHandler = new RequestHandler();

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("ServerEventHandler.handlerAdded()");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void messageReceived(ChannelHandlerContext channelHandlerContext, Event event) throws Exception {
		Channel incoming = channelHandlerContext.channel();
		incoming.write(requestHandler.doHandle(event));

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("ServerEventHandler.exceptionCaught()");
		System.out.println(cause);
		ctx.close();
	}

}
