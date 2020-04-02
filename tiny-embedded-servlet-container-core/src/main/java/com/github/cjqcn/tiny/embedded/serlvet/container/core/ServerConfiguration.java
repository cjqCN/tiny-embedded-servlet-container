package com.github.cjqcn.tiny.embedded.serlvet.container.core;

import java.net.InetSocketAddress;

public interface ServerConfiguration {

    InetSocketAddress address();

    int workersCount();

    int businessCount();

    class Builder {
        private static final InetSocketAddress DEFAULT_ADDRESS = new InetSocketAddress("localhost", 8080);
        private static final int DEFAULT_WORKERS_COUNT = Runtime.getRuntime().availableProcessors() * 2;
        private InetSocketAddress address = DEFAULT_ADDRESS;
        private int workersCount = DEFAULT_WORKERS_COUNT;
        private int businessCount = 0;

        public Builder address(InetSocketAddress address) {
            this.address = address;
            return this;
        }

        public Builder workersCount(int workersCount) {
            this.workersCount = workersCount;
            return this;
        }

        public Builder businessCount(int businessCount) {
            this.businessCount = businessCount;
            return this;
        }

        public ServerConfiguration build() {
            return new ServerConfiguration() {
                @Override
                public InetSocketAddress address() {
                    return address;
                }

                @Override
                public int workersCount() {
                    return workersCount;
                }

                @Override
                public int businessCount() {
                    return businessCount;
                }
            };
        }

    }
}
