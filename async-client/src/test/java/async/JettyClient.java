package async;

import com.google.common.collect.Lists;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;

public class JettyClient extends BaseServerTest {

    private static final String PATH_QUERY_BY_OWNER = "/pets/list/ownerFirstName={ownerFirstName}";

    @Test
    public void testSimpleGet() throws Exception {
        int requestCount = 1000;
        final CountDownLatch allSent = new CountDownLatch(requestCount);
        final CountDownLatch allReceived = new CountDownLatch(requestCount);

        String query = PATH_QUERY_BY_OWNER.replace("{ownerFirstName}", "Test");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                allSent.await(1, TimeUnit.MINUTES);
                return Lists.newArrayList();
            }
        }).when(mockPetDao).loadPets(eq("Test"));


        HttpClient client = new HttpClient();
        client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
//        client.setMaxConnectionsPerAddress(200); // max 200 concurrent connections to every address
//        client.setThreadPool(new QueuedThreadPool(250)); // max 250 threads
//        client.setTimeout(30000); // 30 seconds timeout; if no server reply, the request expires
        client.start();


        final AtomicInteger incomplete = new AtomicInteger();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println();
                        System.out.println("incomplete = " + incomplete);
                        System.out.println();
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }).start();

        for (int i = 0; i < requestCount; i++) {
            ContentExchange exchange = new ContentExchange(true) {
                @Override
                protected void onRequestComplete() throws IOException {
                    allSent.countDown();
                    System.out.println("Request complete");
                }

                protected void onResponseComplete() throws IOException {
                    int status = getResponseStatus();
                    if (status == 200)
                        System.out.println("Response ok");
                    else
                        System.out.println("Response error");
                    allReceived.countDown();
                }

                @Override
                protected void onException(Throwable x) {
                    super.onException(x);
                }
            };
            exchange.setMethod("GET");
            exchange.setURL(getFullQuery(query));

            client.send(exchange);
            System.out.println("Exchange sent");
        }

        assertTrue(allReceived.await(2, TimeUnit.MINUTES));
        client.stop();
    }

    private String getFullQuery(String query) {
        return "http://localhost:8118" + query.replace(" ", "%20");
    }

}
