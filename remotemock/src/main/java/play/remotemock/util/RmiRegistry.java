package play.remotemock.util;

import java.rmi.RemoteException;

public class RmiRegistry {

    private String host;
    private int port;
    //Map<String, Object> registry;

    public RmiRegistry(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public <T> void exportService(T service, String name, Class<? super T> serviceInterface) throws RemoteException {
        RmiUtil.exportService(service, port, name, serviceInterface);
    }

    public <T> T getClientProxy(String name, Class<T> serviceInterface) {
        return RmiUtil.clientProxy(getServiceUrl(name), serviceInterface);
    }

    public String getServiceUrl(String name) {
        return "rmi://" + host + ":" + port + "/" + name;
    }

}
