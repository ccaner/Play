package play.remotemock.util;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.rmi.RemoteException;

public class RmiUtil {

    public static <T> void exportService(T service, int registryPort,
                                         String name, Class<T> serviceInterface) throws RemoteException {
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setRegistryPort(registryPort);
        exporter.setService(service);
        exporter.setServiceName(name);
        exporter.setServiceInterface(serviceInterface);
        exporter.afterPropertiesSet();
        System.out.println("Exposed " + name);
    }

    public static <T> T clientProxy(String rmiUrl, Class<T> serviceInterface) {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceUrl(rmiUrl);
        rmiProxyFactoryBean.setServiceInterface(serviceInterface);
        rmiProxyFactoryBean.afterPropertiesSet();
        return (T) rmiProxyFactoryBean.getObject();

    }
}
