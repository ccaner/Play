package play.baseline.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.*;

public class JettyHandler extends AbstractHandler {

    final RequestMappingHelper mappingHelper;

    final ObjectMapper mapper = new ObjectMapper();

    final ExecutorService asyncExecutor = Executors.newFixedThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Asynch");
        }
    });

    public JettyHandler(Object service) {
        mappingHelper = new RequestMappingHelper(service);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            boolean useContinuation = request.getHeader("Continuation") != null;

            Callable<?> executable = mappingHelper.getExecutableByUri(request.getRequestURI());
            if (executable != null) {

                Object result;
                if (useContinuation) {
                    Continuation continuation = ContinuationSupport.getContinuation(request);
                    if (continuation.isExpired()) {
                        throw new TimeoutException("Request expired");
                    }
                    result = continuation.getAttribute("result");
                    if (result == null) {
                        wrapAsync(executable, request).run();
                        return;
                    }
                } else {
                    result = executable.call();
                }

                if (!(result instanceof String)) {
                    result = mapper.writeValueAsString(result);
                }
                response.setHeader("Content-Type", "text/plain");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(result);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(response.getWriter());
            e.printStackTrace();
        }
        baseRequest.setHandled(true);
    }

    private <T> Runnable wrapAsync(Callable<T> callable, HttpServletRequest request) {
        return new AsyncCallableWrapper<T>(callable, ContinuationSupport.getContinuation(request));
    }

    class AsyncCallableWrapper<T> implements Runnable {

        AsyncCallableWrapper(Callable<T> callable, Continuation continuation) {
            this.callable = callable;
            this.continuation = continuation;
        }

        Callable<T> callable;
        Continuation continuation;

        @Override
        public void run() {
            asyncExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        T result = callable.call();
                        continuation.setAttribute("result", result);
                        continuation.resume();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
       }
    }
}
