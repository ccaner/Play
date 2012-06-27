package play.resultsetmock.jdbc;

import org.junit.*;
import play.baseline.Main;

import java.io.IOException;

/**
 * The system is running in a different VM.
 * we mock out db using a mock datasource.
 *
 */
public class ComponentTest {
    
    private static Process server;
    
    @BeforeClass
    public static void startServer() throws IOException, InterruptedException {
        String classpath = System.getProperty("java.class.path");
        String className = Main.class.getCanonicalName();
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", classpath, className);
        pb.inheritIO();
        server = pb.start();
        Thread.sleep(5000);
    }

    @org.junit.Test
    public void dummy() throws InterruptedException {
    }

    @AfterClass
    public static void stopServer() throws IOException {
        server.destroy();
    }
}
