package com.esphere.iceberg.network.netty;

import com.esphere.iceberg.network.ServerConfig;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NIOServer {

	private final int port;

	public NIOServer(ServerConfig config) {
		this.port = config.getPort();
	}

	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workGroup)
					.channel(NioServerSocketChannel.class).childHandler(new ServerInitializer());
			ChannelFuture future = bootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

}
