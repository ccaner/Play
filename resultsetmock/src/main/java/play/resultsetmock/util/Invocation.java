package play.resultsetmock.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invocation {

    final Object obj;
    final Method method;

    final Object[] args;
    int argsCheck = 0;
    
    
    public Invocation(Object obj, Method method) {
        this.obj = obj;
        this.method = method;

        int paramCount = method.getParameterTypes().length;
        args = new Object[paramCount];
    }

    public boolean setArgument(int index, Object val) {
        args[index - 1] = val;
        int oldArgsCheck = argsCheck;
        argsCheck |= (1 << (index - 1));
        return argsCheck != oldArgsCheck;

    }

    public Object invoke() throws InvocationTargetException, IllegalAccessException {
        if (Integer.bitCount(argsCheck) != method.getParameterTypes().length) {
            throw new RuntimeException("Not all args are set");
        }
        return method.invoke(obj, args);
    }

}

