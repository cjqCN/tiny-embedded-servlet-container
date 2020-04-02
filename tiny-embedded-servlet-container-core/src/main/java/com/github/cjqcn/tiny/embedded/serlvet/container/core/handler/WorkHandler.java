package com.github.cjqcn.tiny.embedded.serlvet.container.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class WorkHandler extends SimpleChannelInboundHandler<HttpServletRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpServletRequest request) {
        ByteBuf hello = Unpooled.wrappedBuffer(StandardCharsets.UTF_8.encode("hello world"));
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, hello);
        HttpUtil.setContentLength(response, hello.readableBytes());
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, hello.readableBytes());
        ctx.writeAndFlush(response);
    }
}
