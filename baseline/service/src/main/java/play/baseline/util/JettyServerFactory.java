package play.baseline.util;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyServerFactory {

    private Object service;

    private int minThreadCount = -1;
    private int maxThreadCount = -1;

    private int httpPort;
    private int httpAcceptors = -1;
//    private int httpSelectors;

    public Server createServer() {
        Server server = new Server();

        final QueuedThreadPool threadPool = new QueuedThreadPool();
        if (minThreadCount != -1) {
            threadPool.setMinThreads(minThreadCount);
        }
        if (maxThreadCount != -1) {
            threadPool.setMaxThreads(maxThreadCount);
        }
        server.setThreadPool(threadPool);

        final SelectChannelConnector connector = new SelectChannelConnector();
        if (httpAcceptors != -1) {
            connector.setAcceptors(httpAcceptors);
        }
        connector.setPort(httpPort);
        server.setConnectors(new Connector[] {connector});

        server.setHandler(new JettyHandler(service));
        connector.setStatsOn(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println();
                        System.out.println(connector.getConnections());
                        System.out.println(threadPool);
                        System.out.println();
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }).start();
        return server;
    }

    public void setMinThreadCount(int minThreadCount) {
        this.minThreadCount = minThreadCount;
    }

    public void setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public void setHttpAcceptors(int httpAcceptors) {
        this.httpAcceptors = httpAcceptors;
    }

    public void setService(Object service) {
        this.service = service;
    }
}
