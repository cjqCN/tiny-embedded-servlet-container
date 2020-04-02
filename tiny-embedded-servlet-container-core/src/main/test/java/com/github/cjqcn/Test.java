package com.github.cjqcn;

import com.github.cjqcn.tiny.embedded.serlvet.container.core.ServerConfiguration;
import com.github.cjqcn.tiny.embedded.serlvet.container.core.TinyServletContainer;
import com.github.cjqcn.tiny.embedded.serlvet.container.core.TinyServletContainerImpl;

import java.net.InetSocketAddress;

public class Test {


    public static void main(String[] args) throws InterruptedException {
        ServerConfiguration serverConfiguration = new ServerConfiguration.Builder()
                .address(new InetSocketAddress("localhost", 8080)).workersCount(8).businessCount(200).build();
        TinyServletContainer tinyServletContainer = new TinyServletContainerImpl(serverConfiguration, null);
        tinyServletContainer.launch();

        Thread.sleep(1000L);
        tinyServletContainer.shutdown();
    }
}
