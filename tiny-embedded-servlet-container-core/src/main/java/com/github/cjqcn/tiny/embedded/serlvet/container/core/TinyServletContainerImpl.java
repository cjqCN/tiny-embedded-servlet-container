package com.github.cjqcn.tiny.embedded.serlvet.container.core;

import com.github.cjqcn.tiny.embedded.serlvet.container.core.handler.ToServletRequestHandler;
import com.github.cjqcn.tiny.embedded.serlvet.container.core.handler.WorkHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TinyServletContainerImpl implements TinyServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(TinyServletContainer.class);

    private final static String HTTP_SERVER_CODEC_HANDLER = "httpServerCodec";
    private final static String HTTP_AGGREGATOR_CODEC_HANDLER = "HttpObjectAggregator";
    private final static String CONTENT_COMPRESSOR_HANDLER = "compressor";
    private final static String KEEP_ALIVE_HANDLER = "keep_alive";
    private final static String CHUNKED_WRITE_HANDLER = "chunkedWriter";
    private final static String TO_SERVLET_REQUEST = "to_servlet_request";
    private final static String WORKER = "work";
    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;
    private final NioEventLoopGroup businessGroup;
    private final ServerConfiguration config;
    private final ServletContext servletContext;
    private volatile TinyServletContainer.State state;
    private ServerBootstrap bootstrap;
    private ChannelGroup channelGroup;
    private ToServletRequestHandler toServletRequestHandler;
    private WorkHandler workHandler;

    public TinyServletContainerImpl(ServerConfiguration configuration, ServletContext servletContext) {
        this.config = configuration;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = config.workersCount() <= 0 ? new NioEventLoopGroup() : new NioEventLoopGroup(config.workersCount());
        this.businessGroup = config.businessCount() <= 0 ? new NioEventLoopGroup() : new NioEventLoopGroup(config.businessCount());
        this.state = State.ALREADY;
        this.servletContext = servletContext;
        this.toServletRequestHandler = new ToServletRequestHandler();
        this.workHandler = new WorkHandler();
    }

    @Override
    public synchronized void launch() {
        if (state != State.ALREADY) {
            throw new IllegalStateException("current state is " + state);
        }

        long start = System.currentTimeMillis();
        channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        bootstrap = createBootstrap(channelGroup);
        Channel serverChannel = null;
        try {
            serverChannel = bootstrap.bind(config.address()).sync().channel();
        } catch (InterruptedException e) {
            logger.error("bind raise exception", e);
            state = State.FAILED;
        }
        channelGroup.add(serverChannel);
        state = State.RUNNING;
        long end = System.currentTimeMillis();
        logger.info("Started TinyServletContainer at address:{}, cost:{}ms", config.address(), end - start);
    }

    private ServerBootstrap createBootstrap(final ChannelGroup channelGroup) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        channelGroup.add(ch);
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(HTTP_SERVER_CODEC_HANDLER, new HttpServerCodec());
                        pipeline.addLast(HTTP_AGGREGATOR_CODEC_HANDLER,
                                new HttpObjectAggregator(10 * 1024 * 1024));
                        pipeline.addLast(CONTENT_COMPRESSOR_HANDLER, new HttpContentCompressor());
                        pipeline.addLast(CHUNKED_WRITE_HANDLER, new ChunkedWriteHandler());
                        pipeline.addLast(KEEP_ALIVE_HANDLER, new HttpServerKeepAliveHandler());
                        pipeline.addLast(TO_SERVLET_REQUEST, toServletRequestHandler);
                        if (businessGroup == null) {
                            pipeline.addLast(WORKER, workHandler);
                        } else {
                            pipeline.addLast(businessGroup, WORKER, workHandler);
                        }
                    }
                });

        return bootstrap;
    }


    @Override
    public synchronized void shutdown() {
        if (state == State.STOPPED) {
            logger.info("Ignore shutdown() call on TinyServletContainer since it has already been stopped.");
            return;
        }
        logger.info("Stopping TinyServletContainer");
        try {
            try {
                channelGroup.close().awaitUninterruptibly();
            } finally {
                try {
                    shutdownExecutorGroups(0, 5, TimeUnit.SECONDS,
                            bootstrap.config().group(), bootstrap.config().childGroup());
                } finally {

                }
            }
        } catch (Throwable t) {
            state = State.FAILED;
            throw t;
        }
        state = State.STOPPED;
        logger.info("Stopped TinyServletContainer on address {}", config.address());
    }

    private void shutdownExecutorGroups(long quietPeriod, long timeout, TimeUnit unit, EventExecutorGroup... groups) {
        Exception ex = null;
        List<Future<?>> futures = new ArrayList<>();
        for (EventExecutorGroup group : groups) {
            if (group == null) {
                continue;
            }
            futures.add(group.shutdownGracefully(quietPeriod, timeout, unit));
        }

        for (Future<?> future : futures) {
            try {
                future.syncUninterruptibly();
            } catch (Exception e) {
                if (ex == null) {
                    ex = e;
                } else {
                    ex.addSuppressed(e);
                }
            }
        }

        if (ex != null) {
            // Just log, don't rethrow since it shouldn't happen normally and
            // there is nothing much can be done from the caller side
            logger.warn("Exception raised when shutting down executor", ex);
        }
    }

    @Override
    public State state() {
        return state;
    }
}
