package com.github.cjqcn.tiny.embedded.serlvet.container.core;

public interface TinyServletContainer {
    void launch();

    void shutdown();

    State state();

    enum State {
        ALREADY,
        RUNNING,
        STOPPED,
        FAILED
    }
}
