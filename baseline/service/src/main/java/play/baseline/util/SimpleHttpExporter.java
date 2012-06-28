package play.baseline.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.support.HandlerMethodResolver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class SimpleHttpExporter implements HttpHandler {

    private Object service;
    private Class<?> serviceInterface;

    private PathMatcher matcher = new AntPathMatcher();

    ObjectMapper mapper = new ObjectMapper();

    public SimpleHttpExporter(Object service, Class<?> serviceInterface) {
        this.service = service;
        this.serviceInterface = serviceInterface;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Callable<?> executable = getExecutableByUri(exchange.getRequestURI());
            if (executable != null) {

                Object result = executable.call();
                if (!(result instanceof String)) {
                    result = mapper.writeValueAsString(result);
                }
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, 0);

                OutputStream os = exchange.getResponseBody();
                os.write(result.toString().getBytes());
                os.flush();
            } else {
                exchange.sendResponseHeaders(404, 0);
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            OutputStream os = exchange.getResponseBody();
            e.printStackTrace(new PrintWriter(os));
            os.flush();
            e.printStackTrace();
        } finally {
            exchange.close();
        }

    }

    private Callable<?> getExecutableByUri(final URI requestURI) {
        Method[] methods = serviceInterface.getMethods();
        for (int i = 0; i < methods.length &&
                methods[i].getAnnotation(RequestMapping.class) != null; i++) {
            final Method method = methods[i];
            RequestMapping rm = method.getAnnotation(RequestMapping.class);
            if (matcher.match(rm.value()[0], requestURI.getPath())) {
                return new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        System.out.println("method = " + getArgs(method, requestURI.getPath())[0]);
                        return method.invoke(service, getArgs(method, requestURI.getPath()));
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
}
