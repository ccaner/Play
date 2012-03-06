package play.remotemock.util;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 3/6/12
 * Time: 1:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class RmiUtil {

    public static <T> void exportService(T service, int registryPort,
                                         String name, Class<? super T> serviceInterface) throws RemoteException {
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setRegistryPort(registryPort);
        exporter.setService(service);
        exporter.setServiceName(name);
        exporter.setServiceInterface(serviceInterface);
        exporter.afterPropertiesSet();
        System.out.println("Exposed " + name);
    }

    public static <T> T clientProxy(String rmiUrl, Class<? super T> serviceInterface) {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceUrl(rmiUrl);
        rmiProxyFactoryBean.setServiceInterface(serviceInterface);
        rmiProxyFactoryBean.afterPropertiesSet();
        return (T) rmiProxyFactoryBean.getObject();

    }
}
