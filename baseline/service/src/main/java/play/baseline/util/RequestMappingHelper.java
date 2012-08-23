package play.baseline.util;

import com.google.common.collect.Maps;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class RequestMappingHelper {

    private final Object service;

    private final PathMatcher matcher = new AntPathMatcher();

    private final Map<String, Method> invokables = Maps.newHashMap();

    public RequestMappingHelper(Object service) {
        this.service = service;
        introspect();
    }

    public Callable<?> getExecutableByUri(final String requestPath) {
        for (final String pat : invokables.keySet()) {
            if (matcher.match(pat, requestPath)) {
                return new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Method method = invokables.get(pat);
                        return method.invoke(service, getArgs(method, requestPath));
                    }
                };
            }
        }
        return null;
    }

    private Object[] getArgs(Method method, String reqPath) {
        RequestMapping rm = method.getAnnotation(RequestMapping.class);
        String pattern = rm.value()[0];
        List<Object> args = new LinkedList<Object>();
        DataBinder binder = new DataBinder(null);

        Map<String, String> vars = matcher.extractUriTemplateVariables(pattern, reqPath);
        Annotation[][] paramAnn = method.getParameterAnnotations();
        for (int i = 0; i < paramAnn.length; i++) {
            PathVariable pathVar = (PathVariable) paramAnn[i][0];
            args.add(binder.convertIfNecessary(vars.get(pathVar.value()), method.getParameterTypes()[i]));
        }
        return args.toArray();
    }

    private void introspect() {
        Method[] methods = service.getClass().getMethods();
        for (Method method : methods) {
            RequestMapping ann = method.getAnnotation(RequestMapping.class);
            if (ann != null) {
                invokables.put(ann.value()[0], method);
            } else {
                // search interfaces
                for (Class<?> iface : service.getClass().getInterfaces()) {
                    try {
                        Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                        ann = equivalentMethod.getAnnotation(RequestMapping.class);
                        if (ann != null) {
                            invokables.put(ann.value()[0], equivalentMethod);
                        }
                    }
                    catch (NoSuchMethodException ex) {
                        // Skip this interface - it doesn't have the method...
                    }
                }
            }
        }
    }

}
