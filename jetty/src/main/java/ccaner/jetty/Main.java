package ccaner.jetty;

import com.sun.jmx.mbeanserver.JmxMBeanServer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 3/5/12
 * Time: 11:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Server server = new Server();
        SelectChannelConnector connector0 = new SelectChannelConnector();
                connector0.setPort(8080);
                connector0.setMaxIdleTime(30000);
                connector0.setRequestHeaderSize(8192);
                connector0.setAcceptors(4);
                connector0.setStatsOn(true);

        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                               HttpServletResponse response) throws IOException, ServletException {
                System.out.println("Main.handle");
            }
        });

        server.setConnectors(new Connector[]{connector0});

        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(8);
        threadPool.setMinThreads(1);
        server.setThreadPool(threadPool);

        server.start();
        server.join();



    }
}
