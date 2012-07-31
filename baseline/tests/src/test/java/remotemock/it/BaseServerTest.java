package remotemock.it;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import play.baseline.Main;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationConfig.xml")
public class BaseServerTest {

    private static Process server;

    @BeforeClass
    public static void startServer() throws IOException, InterruptedException {
        String classpath = System.getProperty("java.class.path");
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String className = Main.class.getCanonicalName();
        ProcessBuilder pb = new ProcessBuilder(javaBin, "-cp", classpath, className);
        pb.inheritIO();
        server = pb.start();
        Thread.sleep(5000);
    }

    @AfterClass
    public static void stopServer() throws IOException, InterruptedException {
        server.destroy();
    }

}
