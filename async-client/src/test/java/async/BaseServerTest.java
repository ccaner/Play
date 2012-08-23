package async;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import play.baseline.Main;
import play.baseline.dao.PetDao;
import play.remotemock.util.RemotableMockFactory;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationConfig.xml")
public class BaseServerTest {

    private static Process server;

    @Autowired
    RemotableMockFactory mockFactory;

    PetDao mockPetDao;

    @BeforeClass
    public static void startServer() throws IOException, InterruptedException {
        String classpath = System.getProperty("java.class.path");
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String className = Main.class.getCanonicalName();
        ProcessBuilder pb = new ProcessBuilder(javaBin, "-cp", classpath, className, "jetty");
        pb.inheritIO();
        server = pb.start();
        Thread.sleep(5000);
    }

    @AfterClass
    public static void stopServer() throws IOException, InterruptedException {
        server.destroy();
    }

    @Before
    public void before() throws RemoteException {
        mockPetDao = mockFactory.mockAndAttach(PetDao.class, "PetDao");
    }


}
