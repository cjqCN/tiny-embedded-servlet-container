package com.github.cjqcn.tiny.embedded.servlet.container.spring;

import com.github.cjqcn.tiny.embedded.serlvet.container.core.ServerConfiguration;
import com.github.cjqcn.tiny.embedded.serlvet.container.core.TinyServletContainerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;

import javax.servlet.ServletContext;

public class TinyEmbeddedServletContainer extends TinyServletContainerImpl implements EmbeddedServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(TinyEmbeddedServletContainer.class.getName());

    public TinyEmbeddedServletContainer(ServerConfiguration configuration, ServletContext servletContext) {
        super(configuration, servletContext);
    }

    @Override
    public void start() throws EmbeddedServletContainerException {
        launch();
    }

    @Override
    public void stop() throws EmbeddedServletContainerException {
        shutdown();
    }

    @Override
    public int getPort() {
        return 8080;
    }
}
