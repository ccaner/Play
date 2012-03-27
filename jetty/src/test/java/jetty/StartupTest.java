package jetty;

import ccaner.jetty.Main;
import org.junit.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 3/8/12
 * Time: 9:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class StartupTest {

    static Main server = new Main();

    @BeforeClass
    public static void startJetty() throws Exception {
        server.startUp(4, 10);
    }

    @Test
    public void startUp() throws IOException {

        URL url = new URL("http://localhost:8080/test");
        URLConnection conn = url.openConnection();
        conn.setReadTimeout(3000);
        InputStream is = conn.getInputStream();
        try {
            while (is.read() != -1) { }
        } catch (SocketTimeoutException e) {
            Assert.fail("Socket timed out");
        }

    }

    @AfterClass
    public static void stopJetty() throws Exception {
       server.shutDown();
    }

}
