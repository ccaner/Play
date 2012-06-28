package play.remotemock.processor;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.remotemock.Remotable;
import play.remotemock.util.InvokeLocalMethodException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * Enhances proxy with a RemotableHelper object. Makes it effectively implement Remotable interface.
 * <p/>
 * Handles Remotable and T(interface) methods
 * <p/>
 * RMI-free
 */
public class RemotableInvocationHandler<T> implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RemotableInvocationHandler.class);

    private final RemotableHelper<T> helper;

    public RemotableInvocationHandler(T localObject, Class<T> serviceInterface) {
        helper = new RemotableHelper<T>(localObject, serviceInterface);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Remotable.class)) {
            //logControl(method, args);
            return method.invoke(helper, args);
        }
        // we volunteer to handle toString or hashCode here
        boolean isServiceMethod = method.getDeclaringClass().isAssignableFrom(helper.serviceInterface);
        if (isServiceMethod && helper.remoteModeOn) {
            try {
                Object result = method.invoke(helper.remoteObject, args);
                logRemote(method, args, result);
                return result;
            } catch (InvocationTargetException e) {
                System.out.println("e = " + e.getCause());
                System.out.println("e5 = " + e.getCause().getCause());
                if (InvokeLocalMethodException.class.isInstance(e.getCause())) {
                    System.out.println("e2 = " + e);
                    Object result = method.invoke(helper.localObject, args);
                    logRedirect(method, args, result);
                    return result;
                }
                throw e.getCause();
            } catch (Exception e) {
                System.out.println("e3 = " + e);
            }
        }
        Object result = method.invoke(helper.localObject, args);
        logLocal(method, args, result);
        return result;
    }

    private static class RemotableHelper<T> implements Remotable<T> {

        volatile T remoteObject;
        final T localObject;
        final Class<T> serviceInterface;
        volatile boolean remoteModeOn = false;

        private RemotableHelper(T localObject, Class<T> serviceInterface) {
            this.localObject = localObject;
            this.serviceInterface = serviceInterface;
        }

        @Override
        public void attachRemote(T remoteObject) {
            RemotableHelper.this.remoteObject = remoteObject;
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

    private void logLocal(Method method, Object[] args, Object result) {
        if (!method.getDeclaringClass().equals(Object.class)) {
            logger.debug("LOCAL: method: [{},{}] response: [{}]",
                    new Object[]{method.getName(), Arrays.toString(args),
                            ReflectionToStringBuilder.toString(result)});
        }
    }

    private void logRedirect(Method method, Object[] args, Object result) {
        logger.debug("REMOTE call directed to local: method: [{},{}] response: [{}]",
                new Object[]{method.getName(), Arrays.toString(args),
                        ReflectionToStringBuilder.toString(result)});
    }

    private void logRemote(Method method, Object[] args, Object result) {
        logger.debug("REMOTE: method: [{},{}] response: [{}]",
                new Object[]{method.getName(), Arrays.toString(args),
                        ReflectionToStringBuilder.toString(result)});
    }

    private void logControl(Method method, Object[] args) {
        logger.trace("CONTROL: method: [{},{}] local class: [{}]",
                new Object[]{method.getName(), Arrays.toString(args),
                        helper.localObject.getClass().getSimpleName()});
    }

}
