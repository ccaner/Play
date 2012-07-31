package play.remotemock.processor;

import org.apache.commons.lang3.ClassUtils;
import play.remotemock.Remotable;
import play.remotemock.util.RmiRegistry;
import play.remotemock.util.RmiUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CANNED FOR NOW
 * Enhances remotable object so remote invocations pass arguments as stubs (instead of serializing arguments)
 */
public class RmiArgumentEnhancer implements InvocationHandler {

    /* need some sort of invocation chaining here */
    private InvocationHandler next;

    private RmiRegistry localRegistry;

    public RmiArgumentEnhancer(InvocationHandler next, RmiRegistry localRegistry) {
        this.next = next;
        this.localRegistry = localRegistry;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("attachRemote")) {
            Object remoteObject = args[0];

            List<Class<?>> interfaces = new ArrayList<Class<?>>();
            interfaces.addAll(ClassUtils.getAllInterfaces(remoteObject.getClass()));
            InvocationHandler handler = new RmiRemoteArgumentConverter(remoteObject);
            Object enhancedRemote = Proxy.newProxyInstance(getClass().getClassLoader(),
                    interfaces.toArray(new Class<?>[interfaces.size()]),
                    handler);

            return next.invoke(proxy, method, new Object[]{enhancedRemote});
        }
        return next.invoke(proxy, method, args);
    }

    private class RmiRemoteArgumentConverter implements InvocationHandler {

        private Object remoteObject;

        RmiRemoteArgumentConverter(Object remoteObject) {
            this.remoteObject = remoteObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object[] enhancedArgs = null;
            if (args != null) {
                enhancedArgs = Arrays.copyOf(args, args.length);
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (!arg.getClass().isPrimitive() && !Modifier.isFinal(arg.getClass().getModifiers())) {
                        enhancedArgs[i] = localRegistry.createStub(arg);
                    }
                }
            }
            return method.invoke(remoteObject, enhancedArgs);
        }
    }

}
