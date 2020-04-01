package com.github.cjqcn.tiny.embedded.serlvet.container.core;

import java.util.concurrent.CompletionStage;

public interface TinyServletContainer {
    CompletionStage<TinyServletContainer> start();

    CompletionStage<TinyServletContainer> suspend();

    CompletionStage<TinyServletContainer> recover();

    CompletionStage<TinyServletContainer> stop();

    boolean isRunning();

    int port();
}
