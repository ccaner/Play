package play.remotemock.mock;

import play.remotemock.mock.util.RmiUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RemotableObjectFactory {

    public static <T> T createRemotableProxy(final T defaultService, final Class<T> serviceInterface) {
        
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

    private static class RemotableProxy<T> implements Remotable {

        private T remoteObject;
        private T defaultObject;
        private Class<T> serviceInterface;

        private boolean remoteModeOn = false;

        private RemotableProxy(Class<T> serviceInterface, T defaultObject) {
            this.serviceInterface = serviceInterface;
            this.defaultObject = defaultObject;
        }

        @Override
        public void attachRemote(String url) {
            remoteObject = RmiUtil.clientProxy(url, serviceInterface);
        }

        public void switchRemoteModeOn() {
            remoteModeOn = true;
        }

        public void switchRemoteModeOff() {
            remoteModeOn = false;
        }

    }

}
