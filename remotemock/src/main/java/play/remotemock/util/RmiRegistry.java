package play.remotemock.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class RmiRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RmiRegistry.class);

    private final String host;
    private final int port;
    private final int servicePort;

    public RmiRegistry(String host, int port, int servicePort) {
        this.host = host;
        this.port = port;
        this.servicePort = servicePort;
    }

    public RmiRegistry(String host, int port) {
        this(host, port, 0);
    }

    public RmiRegistry(int servicePort) {
        this(null, -1, servicePort);
    }

    public <T> void exportService(T service, String serviceName, Class<T> serviceInterface) throws RemoteException {
        RmiUtil.exportService(service, serviceName, serviceInterface, host, port, servicePort);
    }

    public <T> T registerService(T service, Class<T> serviceInterface) throws RemoteException {
        return RmiUtil.registerService(service, serviceInterface, servicePort);
    }

    public <T> T obtainServiceClient(Class<T> service, String serviceName) {
        return RmiUtil.obtainServiceClient(service, serviceName, host, port);
    }

    public String getRmiUrl(String serviceName) {
        return RmiUtil.getRmiUrl(host, port, serviceName);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
