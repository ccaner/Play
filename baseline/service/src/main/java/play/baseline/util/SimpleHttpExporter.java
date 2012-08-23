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

    final RequestMappingHelper mappingHelper;

    final ObjectMapper mapper = new ObjectMapper();

    public SimpleHttpExporter(Object service) {
        mappingHelper = new RequestMappingHelper(service);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Callable<?> executable = mappingHelper.getExecutableByUri(exchange.getRequestURI().getPath());
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

}
