package com.esphere.iceberg.network.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class ClientInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		// pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
		// Delimiters.lineDelimiter()));
		pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast("encoder", new ObjectEncoder());
//        pipeline.addLast("handler", new ChatClientHandler());
		pipeline.addLast("handler2", new ClientEventHandler());

	}
}
