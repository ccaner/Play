package play.remotemock.util;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.rmi.RmiInvocationHandler;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiUtil {

    /**
     * Exports service and binds it to rmi registry
     */
    public static <T> void exportService(T service, String serviceName, Class<T> serviceInterface,
                                         String host, int port, int servicePort) throws RemoteException {
        RmiServiceExporter rmiExporter = new RmiServiceExporter();
        rmiExporter.setServiceInterface(serviceInterface);
        rmiExporter.setServiceName(serviceName);
        rmiExporter.setService(service);
        rmiExporter.setRegistryPort(port);
//        rmiExporter.setRegistryHost(host);
        rmiExporter.setServicePort(servicePort);
        rmiExporter.afterPropertiesSet();
    }

    public static <T> T obtainServiceClient(Class<T> serviceInterface, String serviceName, String host, int port) {
        return obtainServiceClient(serviceInterface, getRmiUrl(host, port, serviceName));
    }

    @SuppressWarnings("unchecked")
    public static <T> T obtainServiceClient(Class<T> serviceInterface, String url) {
        RmiProxyFactoryBean clientFactory = new RmiProxyFactoryBean();
        clientFactory.setServiceInterface(serviceInterface);
        clientFactory.setServiceUrl(url);
        clientFactory.afterPropertiesSet();
        return (T) clientFactory.getObject();
    }

    /*
        This implementation is heavily derived from RmiServiceExporter.

        This method allows us to create stubs of arbitrary java objects (just like RmiServiceExporter does)
        without binding it to any registry. These stubs are suitable for using as rmi callback objects.

        This implementation is dependent on Spring 2.5
        @see http://stackoverflow.com/questions/3779134/spring-two-way-rmi-callback-from-server-executing-on-client-side
     */
    @SuppressWarnings("unchecked")
    public static <T> T createStub(T service, Class<T> serviceInterface, int servicePort) throws RemoteException {
        class UnsafeRmiServiceExporter extends RmiServiceExporter {
            @Override
            public Remote getObjectToExport() {
                return super.getObjectToExport();
            }
            
        }
        UnsafeRmiServiceExporter exporter = new UnsafeRmiServiceExporter();
        exporter.setService(service);
        exporter.setServiceInterface(serviceInterface);
        Remote remoteProxy = exporter.getObjectToExport();

        final RmiInvocationHandler stub = (RmiInvocationHandler) UnicastRemoteObject.
                exportObject(remoteProxy, servicePort);

        class TargetInterfaceWrapper implements MethodInterceptor, Serializable {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                try {
                    return stub.invoke(new RemoteInvocation(invocation));
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        }

        ProxyFactory pf = new ProxyFactory(serviceInterface, new TargetInterfaceWrapper());
        pf.addInterface(Serializable.class);
        return (T) pf.getProxy();
    }

    /* 
        Does the same thing as the overloaded method. But does not require an interface (this means uses cglib)
     */
    @SuppressWarnings("unchecked")
    public static <T> T createStub(T service, int servicePort) throws RemoteException {
        class UnsafeRmiServiceExporter extends RmiServiceExporter {
            @Override
            public Remote getObjectToExport() {
                return super.getObjectToExport();
            }

            @Override
            protected Object getProxyForService() {
                ProxyFactory proxyFactory = new ProxyFactory();
                proxyFactory.setTarget(getService());
                proxyFactory.setOpaque(true);
                return proxyFactory.getProxy(getBeanClassLoader());
            }
        }
        UnsafeRmiServiceExporter exporter = new UnsafeRmiServiceExporter();
        exporter.setService(service);
        Remote remoteProxy = exporter.getObjectToExport();

        final RmiInvocationHandler stub = (RmiInvocationHandler) UnicastRemoteObject.
                exportObject(remoteProxy, servicePort);

        class TargetInterfaceWrapper implements MethodInterceptor, Serializable {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                try {
                    return stub.invoke(new RemoteInvocation(invocation));
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        }

        ProxyFactory pf = new ProxyFactory();
        pf.setTargetClass(service.getClass());
        pf.addInterface(Serializable.class);
        return (T) pf.getProxy();
    }

    public static String getRmiUrl(String host, int port, String serviceName) {
        return "rmi://" + host + ":" + port + "/" + serviceName;
    }
    
    
    

}
