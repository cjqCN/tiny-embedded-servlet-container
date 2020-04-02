package com.github.cjqcn.tiny.embedded.servlet.container.spring;


import com.github.cjqcn.tiny.embedded.serlvet.container.core.ServerConfiguration;
import com.github.cjqcn.tiny.embedded.serlvet.container.core.context.TinyServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.AbstractEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import javax.servlet.ServletException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLClassLoader;

public class EmbeddedTinyFactory extends AbstractEmbeddedServletContainerFactory implements ResourceLoaderAware {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddedTinyFactory.class.getName());
    private ResourceLoader resourceLoader;

    @Override
    public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
        ClassLoader parentClassLoader = resourceLoader != null ? resourceLoader.getClassLoader() : ClassUtils.getDefaultClassLoader();
        TinyServletContext context = new TinyServletContext(getContextPath(), new URLClassLoader(new URL[]{}, parentClassLoader));
        for (ServletContextInitializer initializer : initializers) {
            try {
                initializer.onStartup(context);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        ServerConfiguration serverConfiguration = new ServerConfiguration.Builder()
                .address(new InetSocketAddress(getAddress(), getPort())).build();
        return new TinyEmbeddedServletContainer(serverConfiguration, context);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
