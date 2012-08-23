package async;

import com.google.common.collect.Lists;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;

/**
 * Created with IntelliJ IDEA.
 * User: akpinarc
 * Date: 8/22/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpClient extends BaseServerTest {

    private static final String PATH_QUERY_BY_OWNER = "/pets/list/ownerFirstName={ownerFirstName}";

    @Test
    public void testSimpleGet() throws Exception {
        String query = PATH_QUERY_BY_OWNER.replace("{ownerFirstName}", "Test");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                System.out.println("invocation = " + invocation);
                Thread.sleep(10000);
                return Lists.newArrayList();
            }
        }).when(mockPetDao).loadPets(eq("Test"));


        HttpAsyncClient client = new DefaultHttpAsyncClient();
        client.getParams()
                .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000)
                .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);

        int requestCount = 1000;
        final CountDownLatch stopper = new CountDownLatch(requestCount);
        final AtomicInteger incomplete = new AtomicInteger();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println("incomplete = " + incomplete);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }).start();

        for (int i = 0; i < requestCount; i++) {
            final HttpGet request = new HttpGet("http://www.apache.org/");
            client.execute(request, new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response) {
//                    latch.countDown();
                    System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
                }

                public void failed(final Exception ex) {
//                    latch.countDown();
                    System.out.println(request.getRequestLine() + "->" + ex);
                }

                public void cancelled() {
//                    latch.countDown();
                    System.out.println(request.getRequestLine() + " cancelled");
                }

            });


        }

        stopper.await(20, TimeUnit.SECONDS);
        client.shutdown();
    }

    private String getFullQuery(String query) {
        return "http://localhost:8118" + query.replace(" ", "%20");
    }

}
