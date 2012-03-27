package play.remotemock.processor;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import play.remotemock.Remotable;
import play.remotemock.util.CallLocalMethodException;
import play.remotemock.util.RmiUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RemotableInvocationHandler<T> implements MethodInterceptor {

    private final RemotableHelper<T> remotableHelper;

    public RemotableInvocationHandler(T localObject, Class<T> serviceInterface) {
        remotableHelper = new RemotableHelper<T>(localObject, serviceInterface);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getDeclaringClass().equals(Remotable.class)) {
            return method.invoke(remotableHelper, args);
        }
        Method serviceMethod = remotableHelper.serviceInterface.getMethod(method.getName(),
                method.getParameterTypes());
        if (serviceMethod != null) {
            if (remotableHelper.remoteModeOn) {
                try {
                    return serviceMethod.invoke(remotableHelper.remoteObject, args);
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof CallLocalMethodException) {
                        return serviceMethod.invoke(remotableHelper.localObject, args);
                    }
                    throw e;
                }
            } else {
                return serviceMethod.invoke(remotableHelper.localObject, args);
            }
        }
        return method.invoke(remotableHelper.localObject, args);
    }

    static class RemotableHelper<T> implements Remotable {

        boolean remoteModeOn = false;
        T localObject;
        T remoteObject;
        Class<T> serviceInterface;

        RemotableHelper(T localObject, Class<T> serviceInterface) {
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
