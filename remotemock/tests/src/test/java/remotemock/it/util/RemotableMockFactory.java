package remotemock.it.util;

import org.mockito.Mockito;
import org.mockito.cglib.proxy.Factory;
import play.remotemock.Remotable;
import play.remotemock.util.RmiRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 3/6/12
 * Time: 2:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemotableMockFactory {

    RmiRegistry remoteRegistry;
    RmiRegistry localRegistry;

    public RemotableMockFactory(RmiRegistry remoteRegistry, RmiRegistry localRegistry) {
        this.remoteRegistry = remoteRegistry;
        this.localRegistry = localRegistry;
    }

    public <T> T mockAndAttach(Class<T> serviceInterface, String serviceName) throws RemoteException {
        final T mock = Mockito.mock(serviceInterface);
        final Remotable backend = remoteRegistry.getClientProxy(serviceName + "Remote", Remotable.class);

        Object remotableMock = Proxy.newProxyInstance(RemotableMockFactory.class.getClassLoader(),
                new Class[]{serviceInterface, Factory.class, Remotable.class}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass().equals(Remotable.class)) {
                    return method.invoke(backend, args);
                }
                return method.invoke(mock, args);
            }
        });
        localRegistry.exportService(mock, serviceName, serviceInterface);
        backend.attachRemote(localRegistry.getServiceUrl(serviceName));
        return (T) remotableMock;
    }
}
