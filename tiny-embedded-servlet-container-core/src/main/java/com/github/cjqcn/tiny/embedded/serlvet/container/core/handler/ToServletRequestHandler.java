package com.github.cjqcn.tiny.embedded.serlvet.container.core.handler;


import com.github.cjqcn.tiny.embedded.serlvet.container.core.request.TinyHttpServletRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

@ChannelHandler.Sharable
public class ToServletRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {


        ctx.fireChannelRead(new TinyHttpServletRequest(fullHttpRequest));
    }


}
