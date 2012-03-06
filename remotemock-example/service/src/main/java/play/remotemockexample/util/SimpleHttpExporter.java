package play.remotemockexample.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;

public class SimpleHttpExporter implements HttpHandler {

    Object service;
    Class<?> serviceInterface;

    public SimpleHttpExporter(Object service, Class<?> serviceInterface) {
        this.service = service;
        this.serviceInterface = serviceInterface;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Method method = getMethodByUri(exchange.getRequestURI());
        try {
            if (method != null) {

                Object result = method.invoke(service);
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

    private Method getMethodByUri(URI requestURI) {
        Method[] methods = serviceInterface.getMethods();
        for (int i = 0; i < methods.length &&
                methods[i].getAnnotation(RequestMapping.class) != null; i++) {
            Method method = methods[i];
            RequestMapping rm = method.getAnnotation(RequestMapping.class);
            for (String path : rm.value()) {
                if (requestURI.getPath().endsWith(path)) {
                    return method;
                }
            }
        }
        return null;
    }
}
