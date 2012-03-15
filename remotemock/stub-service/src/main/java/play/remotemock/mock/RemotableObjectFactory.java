package play.remotemock.mock;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import play.remotemock.MyService;
import play.remotemock.MyServiceImpl;
import play.remotemock.stub.StubMyServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.server.RemoteObject;

public class RemotableObjectFactory {

    public static <T> T createRemotableProxy(final T defaultService, final Class<? super T> serviceInterface) {
        
        Object obj = Proxy.newProxyInstance(RemotableObjectFactory.class.getClassLoader(),
                new Class<?>[]{serviceInterface, Remotable.class}, new InvocationHandler() {

            RemotableProxy<T> remoteProxy = new RemotableProxy<T>(serviceInterface, defaultService);

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass().equals(Remotable.class)) {
                    return method.invoke(remoteProxy, args);
                }
                if (remoteProxy.remoteModeOn) {
                    return method.invoke(remoteProxy.remoteObject, args);
                } else {
                    return method.invoke(remoteProxy.defaultObject, args);
                }
            }
        });

        return (T) obj;
    }

    private static class RemotableProxy<T> implements Remotable<T> {

        T remoteObject;
        T defaultObject;
        Class<? super T> serviceInterface;

        boolean remoteModeOn = false;

        private RemotableProxy(Class<? super T> serviceInterface, T defaultObject) {
            this.serviceInterface = serviceInterface;
            this.defaultObject = defaultObject;
        }

        @Override
        public void attachRemote(String url) {
            RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
            rmiProxyFactoryBean.setServiceUrl(url);
            rmiProxyFactoryBean.setServiceInterface(serviceInterface);
            rmiProxyFactoryBean.afterPropertiesSet();
            remoteObject = (T) rmiProxyFactoryBean.getObject();
        }

        public void switchRemoteModeOn() {
            remoteModeOn = true;
        }

        public void switchRemoteModeOff() {
            remoteModeOn = false;
        }

    }

}
