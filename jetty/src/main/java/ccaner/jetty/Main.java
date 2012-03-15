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

    Server server;

    public void startUp(int acceptor, int maxsize) throws Exception {

        server = new Server();
        SelectChannelConnector connector0 = new SelectChannelConnector();
                connector0.setPort(8080);
                connector0.setMaxIdleTime(30000);
                connector0.setRequestHeaderSize(8192);
                connector0.setAcceptors(2);
                connector0.setStatsOn(true);

        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                               HttpServletResponse response) throws IOException, ServletException {
                try {
                    System.out.println(Thread.currentThread().getName());
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }
                response.setStatus(200);
                response.getOutputStream().print("OK");
                response.getOutputStream().flush();
            }
        });

        server.setConnectors(new Connector[]{connector0});

        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(maxsize);
        threadPool.setMinThreads(maxsize);
        server.setThreadPool(threadPool);


        connector0.removeBean(connector0.getSelectorManager());
        server.start();
        //Thread.sleep(10000);
        connector0.getSelectorManager().setSelectSets(4);
        connector0.addBean(connector0.getSelectorManager());

        server.join();

    }
    
    public void shutDown() throws Exception {
        server.stop();
    }
}
