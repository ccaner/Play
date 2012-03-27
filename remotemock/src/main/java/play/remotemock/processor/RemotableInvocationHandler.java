package play.remotemock.processor;

import play.remotemock.Remotable;
import play.remotemock.util.CallLocalMethodException;
import play.remotemock.util.RmiUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RemotableInvocationHandler<T> implements InvocationHandler {

    private final RemotableHelper<T> remotableHelper;

    public RemotableInvocationHandler(T localObject, Class<T> serviceInterface) {
        remotableHelper = new RemotableHelper<>(localObject, serviceInterface);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Remotable.class)) {
            return method.invoke(remotableHelper, args);
        }
        Method serviceMethod = null;
        try {
            serviceMethod = remotableHelper.serviceInterface.getMethod(method.getName(),
                    method.getParameterTypes());
        } catch (NoSuchMethodException ignore) {
        }
        if (serviceMethod != null && remotableHelper.remoteModeOn) {
            try {
                return serviceMethod.invoke(remotableHelper.remoteObject, args);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof CallLocalMethodException) {
                    return serviceMethod.invoke(remotableHelper.localObject, args);
                }
                throw e;
            }
        }
        return method.invoke(remotableHelper.localObject, args);
    }

    private static class RemotableHelper<T> implements Remotable {

        volatile private boolean remoteModeOn = false;
        private T localObject;
        private T remoteObject;
        private Class<T> serviceInterface;

        private RemotableHelper(T localObject, Class<T> serviceInterface) {
            this.localObject = localObject;
            this.serviceInterface = serviceInterface;
        }

        @Override
        public void attachRemote(String rmiUrl) {
            remoteObject = RmiUtil.clientProxy(rmiUrl, serviceInterface);
        }

        @Override
        public void switchRemoteModeOn() {
            remoteModeOn = true;
        }

        @Override
        public void switchRemoteModeOff() {
            remoteModeOn = false;
        }
    }

}
