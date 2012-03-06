package play.remotemock.processor;

import play.remotemock.Remotable;
import play.remotemock.util.RmiUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: caner
 * Date: 3/6/12
 * Time: 4:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemotableInvocationHandler<T> implements InvocationHandler {

    private final RemotableHelper<T> remotableHelper;

    public RemotableInvocationHandler(T localObject, Class<? super T> serviceInterface) {
        remotableHelper = new RemotableHelper<T>(localObject, serviceInterface);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Remotable.class)) {
            return method.invoke(remotableHelper, args);
        }
        Method serviceMethod = remotableHelper.serviceInterface.getMethod(method.getName(),
                method.getParameterTypes());
        if (serviceMethod != null) {
            T bean = remotableHelper.remoteModeOn ? remotableHelper.remoteObject : remotableHelper.localObject;
            return serviceMethod.invoke(bean, args);
        }
        return method.invoke(proxy, args);
    }

    static class RemotableHelper<T> implements Remotable {

        boolean remoteModeOn = false;
        T localObject;
        T remoteObject;
        Class<? super T> serviceInterface;

        RemotableHelper(T localObject, Class<? super T> serviceInterface) {
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
