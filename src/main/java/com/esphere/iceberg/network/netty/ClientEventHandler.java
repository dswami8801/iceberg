package com.esphere.iceberg.network.netty;

import com.esphere.iceberg.payloads.Event;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

/**
 * Created by Dean on 2014/6/25.
 */
public class ClientEventHandler extends ChannelInboundMessageHandlerAdapter<Event> {

    @Override
    public void messageReceived(ChannelHandlerContext channelHandlerContext, Event s) throws Exception {
    	//System.out.println(s);
    }


}
